package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.nguyendevs.ultimateWarpPad.manager.AnimationManager;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;
import org.nguyendevs.ultimateWarpPad.travel.GroupTravelSession;
import org.nguyendevs.ultimateWarpPad.travel.TravelQueue;
import org.nguyendevs.ultimateWarpPad.travel.TravelTask;

import java.util.*;
import java.util.stream.Collectors;

public class WarpSelectionGUI {

    private static final int ITEMS_PER_PAGE = 18;
    private static final int SLOT_PREV = 21;
    private static final int SLOT_BEACON = 22;
    private static final int SLOT_NEXT = 23;
    private static final int SLOT_SETTINGS = 26;

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final AnimationManager animationManager;
    private final org.bukkit.plugin.java.JavaPlugin plugin;
    private final Map<UUID, Warp> openSelections;
    private final Map<UUID, Long> cooldowns;
    private final Map<UUID, Integer> currentPage;
    private final Map<UUID, Integer> filterMode;
    private final TravelQueue travelQueue;
    private SettingsGUI settingsGUI;

    public WarpSelectionGUI(org.bukkit.plugin.java.JavaPlugin plugin, WarpManager warpManager,
            MessageManager messageManager, ConfigManager configManager,
            AnimationManager animationManager, TravelQueue travelQueue) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.animationManager = animationManager;
        this.travelQueue = travelQueue;
        this.openSelections = new HashMap<>();
        this.cooldowns = new HashMap<>();
        this.currentPage = new HashMap<>();
        this.filterMode = new HashMap<>();
    }

    public void updateCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public void setSettingsGUI(SettingsGUI settingsGUI) {
        this.settingsGUI = settingsGUI;
    }

    public void open(Player player, Warp sourceWarp) {
        UUID uuid = player.getUniqueId();
        openSelections.put(uuid, sourceWarp);
        currentPage.put(uuid, 0);
        if (sourceWarp.isAdminWarp()) {
            filterMode.put(uuid, 0);
        } else {
            filterMode.remove(uuid);
        }
        rebuildGUI(player);
    }

    private void rebuildGUI(Player player) {
        UUID uuid = player.getUniqueId();
        Warp sourceWarp = openSelections.get(uuid);
        if (sourceWarp == null)
            return;

        List<Warp> allDestinations = getFilteredDestinations(player, sourceWarp);

        int page = currentPage.getOrDefault(uuid, 0);
        int totalPages = allDestinations.isEmpty() ? 1 : (allDestinations.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        if (page >= totalPages)
            page = totalPages - 1;
        if (page < 0)
            page = 0;
        currentPage.put(uuid, page);

        Inventory inv = Bukkit.createInventory(null, 27,
                messageManager.get("gui.warp_selection.title"));

        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, allDestinations.size());
        for (int i = start; i < end; i++) {
            inv.setItem(i - start, createWarpItem(allDestinations.get(i), sourceWarp));
        }

        if (totalPages > 1) {
            inv.setItem(SLOT_PREV, createSimpleItem(Material.ARROW, "gui.warp_selection.page_previous"));
            inv.setItem(SLOT_NEXT, createSimpleItem(Material.ARROW, "gui.warp_selection.page_next"));
        }

        inv.setItem(SLOT_BEACON, createBeaconItem(player, sourceWarp));

        if (sourceWarp.isOwner(player.getUniqueId())) {
            inv.setItem(SLOT_SETTINGS, createSettingsShortcutItem(sourceWarp));
        }

        player.openInventory(inv);
        player.playSound(player.getLocation(), "minecraft:block.amethyst_block.resonate", SoundCategory.AMBIENT, 1.0f,
                1.0f);
    }

    private ItemStack createSimpleItem(Material mat, String namePath) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get(namePath).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createBeaconItem(Player player, Warp sourceWarp) {
        UUID uuid = player.getUniqueId();
        boolean isAdmin = sourceWarp.isAdminWarp();

        int mode = isAdmin ? filterMode.getOrDefault(uuid, 0) : 0;
        List<String> modeNames = messageManager.getRawList("gui.warp_selection.filter_modes");

        Material mat = isAdmin ? getModeIcon(mode) : Material.BEACON;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        if (isAdmin) {
            String modeName = mode < modeNames.size() ? modeNames.get(mode) : "Unknown";
            meta.displayName(messageManager.get("gui.warp_selection.beacon.admin_name")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(messageManager.getComponentList("gui.warp_selection.beacon.admin_lore",
                    Map.of("mode", modeName)).stream()
                    .map(c -> c.decoration(TextDecoration.ITALIC, false))
                    .collect(Collectors.toList()));
        } else {
            String type = sourceWarp.isOwner(uuid) ? "Your warps" : "Trusted warps";
            meta.displayName(messageManager.get("gui.warp_selection.beacon.player_name")
                    .decoration(TextDecoration.ITALIC, false));
            meta.lore(messageManager.getComponentList("gui.warp_selection.beacon.player_lore",
                    Map.of("type", type)).stream()
                    .map(c -> c.decoration(TextDecoration.ITALIC, false))
                    .collect(Collectors.toList()));
        }

        item.setItemMeta(meta);
        return item;
    }

    private Material getModeIcon(int mode) {
        return switch (mode) {
            case 0 -> Material.BEACON;
            case 1 -> Material.NETHER_STAR;
            default -> Material.TOTEM_OF_UNDYING;
        };
    }

    private List<Warp> getFilteredDestinations(Player player, Warp sourceWarp) {
        UUID uuid = player.getUniqueId();

        if (sourceWarp.isAdminWarp()) {
            int mode = filterMode.getOrDefault(uuid, 0);
            List<Warp> all = warpManager.getAvailableDestinations(sourceWarp, player);

            return switch (mode) {
                case 0 -> all.stream().filter(w -> w.getType() == WarpType.ADMIN).collect(Collectors.toList());
                case 1 -> all.stream().filter(w -> w.getType() == WarpType.PLAYER && w.isOwner(uuid))
                        .collect(Collectors.toList());
                case 2 -> all.stream().filter(w -> w.getType() == WarpType.PLAYER && w.isPublic())
                        .collect(Collectors.toList());
                default -> all;
            };
        }

        if (sourceWarp.isOwner(uuid)) {
            return warpManager.getAvailableDestinations(sourceWarp, player);
        }

        if (sourceWarp.canPlayerUse(uuid)) {
            UUID owner = sourceWarp.getOwner();
            return warpManager.getAllWarps().stream()
                    .filter(w -> w.getOwner() != null && w.getOwner().equals(owner))
                    .filter(w -> w.canPlayerUse(uuid))
                    .filter(w -> !w.getCompositeId().equals(sourceWarp.getCompositeId()))
                    .filter(w -> warpManager.isInRange(sourceWarp, w))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private ItemStack createWarpItem(Warp warp, Warp sourceWarp) {
        boolean busy = travelQueue.isBusy(warp);
        Material icon = busy ? Material.ENDER_PEARL : (warp.getIcon() != null ? warp.getIcon() : Material.NETHER_STAR);
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(messageManager.buildComponent(warp.getWarpName())
                .decoration(TextDecoration.ITALIC, false));

        Warp costWarp = sourceWarp.isAdminWarp() ? sourceWarp : warp;
        String lorePath = busy ? "gui.warp_selection.lore.busy"
                : (costWarp.getCost() < 0 ? "gui.warp_selection.lore.free" : "gui.warp_selection.lore.paid");

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("world", warp.getWorld().getName());
        placeholders.put("x", String.valueOf((int) warp.getX()));
        placeholders.put("y", String.valueOf((int) warp.getY()));
        placeholders.put("z", String.valueOf((int) warp.getZ()));
        placeholders.put("amount", String.valueOf((int) costWarp.getCost()));
        placeholders.put("type", costWarp.getCostType().name());

        List<Component> lore = messageManager.getComponentList(lorePath, placeholders).stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList());

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createSettingsShortcutItem(Warp warp) {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.warp_selection.settings_shortcut.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.warp_selection.settings_shortcut.warp_name",
                Map.of("name", messageManager.translateColorCodes(warp.getWarpName())))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.warp_selection.settings_shortcut.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public boolean handleClick(Player player, int slot) {
        Warp sourceWarp = openSelections.get(player.getUniqueId());
        if (sourceWarp == null)
            return false;

        player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

        if (slot == SLOT_BEACON) {
            if (sourceWarp.isAdminWarp()) {
                UUID uuid = player.getUniqueId();
                int mode = filterMode.getOrDefault(uuid, 0);
                filterMode.put(uuid, (mode + 1) % 3);
            }
            rebuildGUI(player);
            return true;
        }

        if (slot == SLOT_PREV) {
            UUID uuid = player.getUniqueId();
            int page = currentPage.getOrDefault(uuid, 0);
            if (page > 0) {
                currentPage.put(uuid, page - 1);
                rebuildGUI(player);
            }
            return true;
        }

        if (slot == SLOT_NEXT) {
            UUID uuid = player.getUniqueId();
            int page = currentPage.getOrDefault(uuid, 0);
            int pageCount = getPageCount(player);
            if (page < pageCount - 1) {
                currentPage.put(uuid, page + 1);
                rebuildGUI(player);
            }
            return true;
        }

        if (slot == SLOT_SETTINGS) {
            if (sourceWarp.isOwner(player.getUniqueId()) && settingsGUI != null) {
                settingsGUI.open(player, sourceWarp);
            }
            return true;
        }

        if (slot < 0 || slot >= ITEMS_PER_PAGE)
            return true;

        int page = currentPage.getOrDefault(player.getUniqueId(), 0);
        List<Warp> destinations = getFilteredDestinations(player, sourceWarp);
        int index = page * ITEMS_PER_PAGE + slot;
        if (index >= destinations.size())
            return true;

        Warp destination = destinations.get(index);

        if (!travelQueue.tryClaim(destination)) {
            travelQueue.enqueue(player, sourceWarp, destination);
            messageManager.send(player, "gui.warp_selection.warp_busy");
            rebuildGUI(player);
            return true;
        }

        Warp costWarp = sourceWarp.isAdminWarp() ? sourceWarp : destination;
        if (!warpManager.canAfford(player, costWarp)) {
            travelQueue.onComplete(destination);
            String costType = costWarp.getCostType().name().toLowerCase();
            int amount = (int) costWarp.getCost();
            int missing = costWarp.getCostType() == CostType.XP
                    ? warpManager.getMissingXp(player, costWarp)
                    : 0;
            messageManager.send(player, "cost.not_enough_" + costType,
                    Map.of("amount", String.valueOf(amount),
                            "missing", String.valueOf(missing)));
            return true;
        }

        int cd = configManager.getCooldown();
        if (cd > 0) {
            long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
            long elapsed = (System.currentTimeMillis() - last) / 1000;
            if (elapsed < cd) {
                travelQueue.onComplete(destination);
                messageManager.sendTravel(player, "cooldown",
                        Map.of("time", String.valueOf(cd - elapsed)));
                return true;
            }
        }

        player.closeInventory();
        openSelections.remove(player.getUniqueId());

        startTravel(player, sourceWarp, destination);
        return true;
    }

    private int getPageCount(Player player) {
        Warp sourceWarp = openSelections.get(player.getUniqueId());
        if (sourceWarp == null)
            return 0;
        List<Warp> destinations = getFilteredDestinations(player, sourceWarp);
        return (destinations.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
    }

    public void startTravel(Player player, Warp source, Warp destination) {
        boolean sourceOk = source.isAdminWarp()
                ? warpManager.isSkyClearForAdmin(source.getLocation())
                : warpManager.isSkyClear(source.getLocation());
        if (!sourceOk) {
            messageManager.send(player, "error.blocked_above");
            travelQueue.onComplete(destination);
            return;
        }

        boolean destOk = destination.isAdminWarp()
                ? warpManager.isSkyClearForAdmin(destination.getLocation())
                : warpManager.isSkyClear(destination.getLocation());
        if (!destOk) {
            messageManager.send(player, "error.dest_blocked");
            travelQueue.onComplete(destination);
            return;
        }

        Warp costWarp = source.isAdminWarp() ? source : destination;
        if (!warpManager.deductCost(player, costWarp)) {
            travelQueue.onComplete(destination);
            return;
        }

        if (costWarp.getCost() >= 0) {
            messageManager.send(player, "cost.deducted_" + costWarp.getCostType().name().toLowerCase(),
                    Map.of("amount", String.valueOf((int) costWarp.getCost())));
        }

        Location sourceLoc = source.getLocation();
        sourceLoc.getWorld().playSound(sourceLoc, "minecraft:block.beacon.activate", SoundCategory.AMBIENT, 1.0f, 0.5f);
        sourceLoc.getWorld().playSound(sourceLoc, "minecraft:block.beacon.power_select", SoundCategory.AMBIENT, 0.5f,
                0.8f);
        sourceLoc.getWorld().playSound(sourceLoc, "minecraft:ambient.basalt_deltas.loop", SoundCategory.AMBIENT, 1.0f,
                0.5f);
        sourceLoc.getWorld().playSound(sourceLoc, "minecraft:ambient.soul_sand_valley.mood", SoundCategory.AMBIENT,
                1.0f, 0.5f);

        animationManager.playAnimation(source);

        if (configManager.isGroupTeleporting()) {
            startGroupTravel(player, source, destination);
        } else {
            startSingleTravel(player, source, destination);
        }
    }

    private void startSingleTravel(Player player, Warp source, Warp destination) {
        double centerX = Math.floor(source.getX()) + 0.5;
        double centerZ = Math.floor(source.getZ()) + 0.5;
        double relX = player.getX() - centerX;
        double relZ = player.getZ() - centerZ;

        if (configManager.isCenter()) {
            Location snapLoc = player.getLocation().clone();
            snapLoc.setX(centerX);
            snapLoc.setY(source.getY() + (source.isAdminWarp() ? 2 : 1));
            snapLoc.setZ(centerZ);
            player.teleport(snapLoc);
        }

        messageManager.sendTravel(player, "start");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 150, 0, false, false));
            if (configManager.isApplyDarkness()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 0, false, false));
            }
            if (configManager.isApplyGlowing()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
            }
            if (configManager.isApplyRegeneration()) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false));
            }
            if (configManager.isApplyVanish()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    if (player.isOnline() && player.hasPotionEffect(PotionEffectType.LEVITATION)) {
                        player.addPotionEffect(
                                new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false, false));
                    }
                }, 40L);
            }
            new TravelTask(plugin, player, source, destination, configManager,
                    animationManager, messageManager, travelQueue, relX, relZ)
                    .runTaskTimer(plugin, 0L, 1L);
        }, 30L);
    }

    private void startGroupTravel(Player clicker, Warp source, Warp destination) {
        int bx = (int) Math.floor(source.getX());
        int bz = (int) Math.floor(source.getZ());
        Set<Player> group = new HashSet<>();
        group.add(clicker);

        int maxPerWarp = configManager.getGroupMaxPerWarp(); // -1 = unlimited

        for (Player nearby : source.getWorld().getPlayers()) {
            if (nearby.equals(clicker))
                continue;
            if (maxPerWarp > 0 && group.size() >= maxPerWarp)
                break;
            if (Math.abs(nearby.getLocation().getBlockX() - bx) <= 1
                    && Math.abs(nearby.getLocation().getBlockZ() - bz) <= 1) {
                // Skip players already waiting in a travel queue — they will be
                // teleported by their own queued entry; dragging them along would
                // cause a double-teleport back-and-forth.
                if (travelQueue.isQueued(nearby)) continue;
                group.add(nearby);
            }
        }

        double centerX = Math.floor(source.getX()) + 0.5;
        double centerZ = Math.floor(source.getZ()) + 0.5;

        GroupTravelSession session = new GroupTravelSession(group, source, destination,
                animationManager, plugin, travelQueue);

        int delay = 0;
        for (Player member : group) {
            double relX = member.getX() - centerX;
            double relZ = member.getZ() - centerZ;

            if (configManager.isCenter()) {
                Location snapLoc = member.getLocation().clone();
                snapLoc.setX(centerX);
                snapLoc.setY(source.getY() + (source.isAdminWarp() ? 2 : 1));
                snapLoc.setZ(centerZ);
                member.teleport(snapLoc);
            }

            final int startOffset = delay;
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (Math.abs(member.getLocation().getBlockX() - bx) > 1
                        || Math.abs(member.getLocation().getBlockZ() - bz) > 1) {
                    session.removePlayer(member);
                    return;
                }
                member.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 150, 0, false, false));
                if (configManager.isApplyDarkness()) {
                    member.addPotionEffect(
                            new PotionEffect(PotionEffectType.DARKNESS, Integer.MAX_VALUE, 0, false, false));
                }
                if (configManager.isApplyGlowing()) {
                    member.addPotionEffect(
                            new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false));
                }
                if (configManager.isApplyRegeneration()) {
                    member.addPotionEffect(
                            new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0, false, false));
                }
                if (configManager.isApplyVanish()) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (member.isOnline() && member.hasPotionEffect(PotionEffectType.LEVITATION)) {
                            member.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0,
                                    false, false));
                        }
                    }, 40L);
                }
                messageManager.sendTravel(member, "start");
                new TravelTask(plugin, member, source, destination, configManager,
                        animationManager, messageManager, session, travelQueue, relX, relZ)
                        .runTaskTimer(plugin, 0L, 1L);
            }, 30L + startOffset);

            delay += configManager.getGroupDelayInTick();
        }
    }

    public void close(Player player) {
        UUID uuid = player.getUniqueId();
        openSelections.remove(uuid);
        currentPage.remove(uuid);
        filterMode.remove(uuid);
    }

    public boolean hasOpen(Player player) {
        return openSelections.containsKey(player.getUniqueId());
    }

    public void cleanup(Player player) {
        UUID uuid = player.getUniqueId();
        openSelections.remove(uuid);
        currentPage.remove(uuid);
        filterMode.remove(uuid);
    }
}
