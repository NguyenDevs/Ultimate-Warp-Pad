package org.nguyendevs.ultimateWarpPad.manager;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.nguyendevs.ultimateWarpPad.database.DatabaseManager;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;
import org.nguyendevs.ultimateWarpPad.schematic.AdminWarpSchematicData;
import org.nguyendevs.ultimateWarpPad.schematic.WarpSchematicData;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WarpManager {

    private final JavaPlugin plugin;
    private final DatabaseManager database;
    private final ConfigManager configManager;
    private final AnimationManager animationManager;
    private final Map<String, Warp> warps;
    private final Map<Long, List<String>> warpByChunk;

    public WarpManager(JavaPlugin plugin, DatabaseManager database,
                       ConfigManager configManager, AnimationManager animationManager) {
        this.plugin = plugin;
        this.database = database;
        this.configManager = configManager;
        this.animationManager = animationManager;
        this.warps = new ConcurrentHashMap<>();
        this.warpByChunk = new HashMap<>();
    }

    private static long chunkKey(int chunkX, int chunkZ) {
        return ((long) chunkX << 32) | (chunkZ & 0xFFFFFFFFL);
    }

    private void indexWarp(Warp warp) {
        int bx = (int) Math.floor(warp.getX());
        int bz = (int) Math.floor(warp.getZ());
        int half = warp.isAdminWarp() ? 3 : 2;
        int minCX = (bx - half) >> 4;
        int maxCX = (bx + half) >> 4;
        int minCZ = (bz - half) >> 4;
        int maxCZ = (bz + half) >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                warpByChunk.computeIfAbsent(chunkKey(cx, cz), k -> new ArrayList<>()).add(warp.getCompositeId());
            }
        }
    }

    private void removeWarpIndex(Warp warp) {
        int bx = (int) Math.floor(warp.getX());
        int bz = (int) Math.floor(warp.getZ());
        int half = warp.isAdminWarp() ? 3 : 2;
        int minCX = (bx - half) >> 4;
        int maxCX = (bx + half) >> 4;
        int minCZ = (bz - half) >> 4;
        int maxCZ = (bz + half) >> 4;

        for (int cx = minCX; cx <= maxCX; cx++) {
            for (int cz = minCZ; cz <= maxCZ; cz++) {
                long key = chunkKey(cx, cz);
                List<String> ids = warpByChunk.get(key);
                if (ids != null) {
                    ids.remove(warp.getCompositeId());
                    if (ids.isEmpty()) {
                        warpByChunk.remove(key);
                    }
                }
            }
        }
    }

    public CompletableFuture<Void> loadAllWarps() {
        return database.loadAllWarps().thenAcceptAsync(loaded -> {
            for (Warp warp : loaded) {
                warps.put(warp.getCompositeId(), warp);
                indexWarp(warp);
            }
            plugin.getLogger().info("Loaded " + loaded.size() + " warps");
        });
    }

    public Warp getWarp(String compositeId) {
        return warps.get(compositeId);
    }

    public Warp getWarp(UUID owner, String warpId) {
        UUID id = (owner == null) ? Warp.ADMIN_UUID : owner;
        return warps.get(id.toString() + "_" + warpId);
    }

    public Collection<Warp> getAllWarps() {
        return warps.values();
    }

    public List<Warp> getAdminWarps() {
        return warps.values().stream()
                .filter(Warp::isAdminWarp)
                .collect(Collectors.toList());
    }

    public List<Warp> getPlayerWarps(UUID owner) {
        return warps.values().stream()
                .filter(w -> w.getOwner() != null && w.getOwner().equals(owner))
                .collect(Collectors.toList());
    }

    public Warp getWarpAtLocation(Location location) {
        int cx = location.getBlockX() >> 4;
        int cz = location.getBlockZ() >> 4;
        List<String> ids = warpByChunk.get(chunkKey(cx, cz));
        if (ids == null) return null;

        for (String id : ids) {
            Warp warp = warps.get(id);
            if (warp == null || !warp.getWorld().equals(location.getWorld())) continue;
            int wx = (int) Math.floor(warp.getX());
            int wy = (int) Math.floor(warp.getY());
            int wz = (int) Math.floor(warp.getZ());
            int lx = location.getBlockX();
            int ly = location.getBlockY();
            int lz = location.getBlockZ();
            int half = warp.isAdminWarp() ? 3 : 2;
            int halfY = warp.isAdminWarp() ? 2 : 1;
            if (Math.abs(lx - wx) <= half && Math.abs(ly - wy) <= halfY && Math.abs(lz - wz) <= half) {
                return warp;
            }
        }
        return null;
    }

    public List<Warp> getAvailableDestinations(Warp sourceWarp, Player player) {
        UUID playerUUID = player.getUniqueId();

        if (sourceWarp.isAdminWarp()) {
            List<Warp> destinations = new ArrayList<>(getAdminWarps());
            destinations.remove(sourceWarp);
            destinations.addAll(getPlayerWarps(playerUUID));
            return filterByRange(destinations, sourceWarp);
        }

        if (sourceWarp.isOwner(playerUUID)) {
            List<Warp> destinations = new ArrayList<>(getPlayerWarps(playerUUID));
            destinations.remove(sourceWarp);
            return filterByRange(destinations, sourceWarp);
        }

        if (sourceWarp.canPlayerUse(playerUUID)) {
            UUID owner = sourceWarp.getOwner();
            return warps.values().stream()
                    .filter(w -> w.getOwner() != null && w.getOwner().equals(owner))
                    .filter(w -> w.canPlayerUse(playerUUID))
                    .filter(w -> !w.getCompositeId().equals(sourceWarp.getCompositeId()))
                    .filter(w -> isInRange(sourceWarp, w))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private List<Warp> filterByRange(List<Warp> destinations, Warp source) {
        return destinations.stream()
                .filter(d -> isInRange(source, d))
                .collect(Collectors.toList());
    }

    public boolean isInRange(Warp source, Warp target) {
        if (source.getRange() < 0) return true;
        if (!source.getWorld().equals(target.getWorld())) return false;
        double dist = source.getLocation().distance(target.getLocation());
        return dist <= source.getRange();
    }

    public boolean canAfford(Player player, Warp warp) {
        if (warp.getCost() < 0) return true;
        return switch (warp.getCostType()) {
            case FREE -> true;
            case XP -> getTotalExperience(player) >= (int) warp.getCost();
            case MONEY -> {
                if (!plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
                    yield false;
                }
                var rsp = plugin.getServer().getServicesManager().getRegistration(
                        net.milkbowl.vault.economy.Economy.class);
                yield rsp != null && rsp.getProvider().has(player, warp.getCost());
            }
        };
    }

    public int getMissingXp(Player player, Warp warp) {
        int cost = (int) warp.getCost();
        int have = getTotalExperience(player);
        return Math.max(0, cost - have);
    }

    public boolean deductCost(Player player, Warp warp) {
        if (warp.getCost() < 0) return true;
        return switch (warp.getCostType()) {
            case FREE -> true;
            case XP -> {
                int total = getTotalExperience(player);
                setTotalExperience(player, total - (int) warp.getCost());
                yield true;
            }
            case MONEY -> {
                var rsp = plugin.getServer().getServicesManager().getRegistration(
                        net.milkbowl.vault.economy.Economy.class);
                if (rsp != null) {
                    rsp.getProvider().withdrawPlayer(player, warp.getCost());
                    yield true;
                }
                yield false;
            }
        };
    }

    private int getTotalExperience(Player player) {
        int level = player.getLevel();
        int total = 0;
        for (int i = 0; i < level; i++) {
            total += getXpNeededForNextLevel(i);
        }
        total += (int) (player.getExp() * getXpNeededForNextLevel(level));
        return total;
    }

    private int getXpNeededForNextLevel(int level) {
        if (level >= 30) return 62 + (level - 30) * 7;
        if (level >= 15) return 37 + (level - 15) * 5;
        return 7 + level * 2;
    }

    private void setTotalExperience(Player player, int totalXp) {
        int level = 0;
        int remaining = totalXp;
        while (true) {
            int needed = getXpNeededForNextLevel(level);
            if (remaining < needed) break;
            remaining -= needed;
            level++;
        }
        player.setLevel(level);
        player.setExp(level == 0 || remaining <= 0 ? 0 : (float) remaining / getXpNeededForNextLevel(level));
    }

    public boolean createWarp(Warp warp) {
        String cid = warp.getCompositeId();
        if (warps.containsKey(cid)) return false;

        if (warp.getOwner() != null) {
            int max = configManager.getMaxWarpsPerPlayer();
            if (getPlayerWarpCount(warp.getOwner()) >= max) return false;
        }

        warp.setSchematicVariant(0);

        World world = warp.getWorld();
        int cx = (int) Math.floor(warp.getX());
        int cy = (int) Math.floor(warp.getY());
        int cz = (int) Math.floor(warp.getZ());

        if (warp.isAdminWarp()) {
            int ox = cx - 3;
            int oy = cy - 1;
            int oz = cz - 3;
            List<String> terrainSnapshot = AdminWarpSchematicData.captureArea(world, ox, oy, oz);
            AdminWarpSchematicData.paste(world, ox, oy, oz, warp.getSchematicVariant());
            database.saveTerrainSnapshot(cid, terrainSnapshot);
        } else {
            int ox = cx - 2;
            int oy = cy - 2;
            int oz = cz - 2;
            List<String> terrainSnapshot = WarpSchematicData.captureArea(world, ox, oy, oz);
            WarpSchematicData.paste(world, ox, oy, oz, warp.getSchematicVariant());
            database.saveTerrainSnapshot(cid, terrainSnapshot);
        }

        createWorldGuardRegion(warp);
        warps.put(cid, warp);
        indexWarp(warp);
        database.saveWarp(warp);
        return true;
    }

    public void deleteWarp(Warp warp) {
        animationManager.cancelAnimation(warp);

        World world = warp.getWorld();
        int cx = (int) Math.floor(warp.getX());
        int cy = (int) Math.floor(warp.getY());
        int cz = (int) Math.floor(warp.getZ());

        boolean isAdmin = warp.isAdminWarp();
        int ox = cx - (isAdmin ? 3 : 2);
        int oy = cy - (isAdmin ? 1 : 2);
        int oz = cz - (isAdmin ? 3 : 2);

        removeWorldGuardRegion(warp);
        removeWarpIndex(warp);
        warps.remove(warp.getCompositeId());

        database.loadTerrainSnapshot(warp.getCompositeId()).thenAccept(snapshot -> {
            if (!snapshot.isEmpty()) {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (isAdmin) {
                        AdminWarpSchematicData.restoreArea(world, ox, oy, oz, snapshot);
                    } else {
                        WarpSchematicData.restoreArea(world, ox, oy, oz, snapshot);
                    }
                });
            } else {
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (isAdmin) {
                        AdminWarpSchematicData.clearArea(world, ox, oy, oz);
                    } else {
                        WarpSchematicData.clearArea(world, ox, oy, oz, warp.getSchematicVariant());
                    }
                });
                plugin.getLogger().warning("No terrain snapshot found for warp "
                        + warp.getCompositeId() + ", falling back to clearArea.");
            }
            database.deleteWarp(warp);
        });
    }

    public void saveWarp(Warp warp) {
        warps.put(warp.getCompositeId(), warp);
        database.saveWarp(warp);
    }

    private void createWorldGuardRegion(Warp warp) {
        try {
            World world = warp.getWorld();
            String regionName = "uwp_" + warp.getCompositeId();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm == null) return;

            int x = (int) Math.floor(warp.getX());
            int y = (int) Math.floor(warp.getY());
            int z = (int) Math.floor(warp.getZ());

            BlockVector3 min, max;
            if (warp.isAdminWarp()) {
                min = BlockVector3.at(x - 3, y - 1, z - 3);
                max = BlockVector3.at(x + 3, y + 1, z + 3);
            } else {
                min = BlockVector3.at(x - 2, y - 2, z - 2);
                max = BlockVector3.at(x + 2, y, z + 2);
            }
            ProtectedCuboidRegion region = new ProtectedCuboidRegion(regionName, min, max);

            region.setFlag(Flags.BLOCK_BREAK, StateFlag.State.DENY);
            region.setFlag(Flags.BLOCK_PLACE, StateFlag.State.DENY);

            rm.addRegion(region);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to create WorldGuard region: " + e.getMessage());
        }
    }

    private void removeWorldGuardRegion(Warp warp) {
        try {
            World world = warp.getWorld();
            String regionName = "uwp_" + warp.getCompositeId();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm != null) {
                rm.removeRegion(regionName);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to remove WorldGuard region: " + e.getMessage());
        }
    }

    public void removeAllRegions() {
        try {
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            for (Warp warp : warps.values()) {
                World world = warp.getWorld();
                RegionManager rm = container.get(BukkitAdapter.adapt(world));
                if (rm != null) {
                    rm.removeRegion("uwp_" + warp.getCompositeId());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to remove regions: " + e.getMessage());
        }
    }

    public int getPlayerWarpCount(UUID playerUUID) {
        return (int) warps.values().stream()
                .filter(w -> w.getOwner() != null && w.getOwner().equals(playerUUID))
                .count();
    }

    public int getMaxWarpsPerPlayer() {
        return configManager.getMaxWarpsPerPlayer();
    }

    public boolean isWarpIdTaken(UUID owner, String warpId) {
        if (owner == null) {
            return warps.values().stream()
                    .anyMatch(w -> w.isAdminWarp() && w.getWarpId().equals(warpId));
        }
        return warps.values().stream()
                .anyMatch(w -> w.getOwner() != null && w.getOwner().equals(owner)
                        && w.getWarpId().equals(warpId));
    }

    public List<String> getAdminWarpIds() {
        return getAdminWarps().stream()
                .map(Warp::getWarpId)
                .collect(Collectors.toList());
    }

    public List<String> getPlayerWarpIds(UUID owner) {
        return getPlayerWarps(owner).stream()
                .map(Warp::getWarpId)
                .collect(Collectors.toList());
    }

    public List<String> getAllAccessibleWarpIds(Player player) {
        List<String> ids = new ArrayList<>();
        for (Warp warp : warps.values()) {
            if (warp.canPlayerUse(player.getUniqueId())) {
                ids.add(warp.getWarpId());
            }
        }
        return ids;
    }

    public static String toRoman(int n) {
        if (n <= 0 || n > 100) return String.valueOf(n);
        String[] symbols = {"C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        int[] values = {100, 90, 50, 40, 10, 9, 5, 4, 1};
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (n >= values[i]) {
                result.append(symbols[i]);
                n -= values[i];
            }
        }
        return result.toString();
    }

    public Warp getOverlappingWarp(Location location) {
        return getOverlappingWarpInternal(location, false);
    }

    public Warp getOverlappingWarpForAdmin(Location location) {
        return getOverlappingWarpInternal(location, true);
    }

    private Warp getOverlappingWarpInternal(Location location, boolean newIsAdmin) {
        int nx = location.getBlockX();
        int ny = location.getBlockY();
        int nz = location.getBlockZ();

        for (Warp existing : warps.values()) {
            if (!existing.getWorld().equals(location.getWorld())) continue;
            int ex = (int) Math.floor(existing.getX());
            int ey = (int) Math.floor(existing.getY());
            int ez = (int) Math.floor(existing.getZ());
            int threshXZ = 9;
            int threshY = 2;

            if (Math.abs(nx - ex) <= threshXZ && Math.abs(ny - ey) <= threshY && Math.abs(nz - ez) <= threshXZ) {
                return existing;
            }
        }
        return null;
    }

    public boolean isSkyClear(Location location) {
        return isSkyClearInternal(location, 1, 1);
    }

    public boolean isSkyClearForAdmin(Location location) {
        return isSkyClearInternal(location, 2, 1);
    }

    private boolean isSkyClearInternal(Location location, int startYOffset, int xzRadius) {
        World world = location.getWorld();
        int bx = location.getBlockX();
        int by = location.getBlockY();
        int bz = location.getBlockZ();
        int maxY = world.getMaxHeight();

        for (int x = bx - xzRadius; x <= bx + xzRadius; x++) {
            for (int z = bz - xzRadius; z <= bz + xzRadius; z++) {
                for (int y = by + startYOffset; y < maxY; y++) {
                    if (!world.getBlockAt(x, y, z).getType().isAir()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
