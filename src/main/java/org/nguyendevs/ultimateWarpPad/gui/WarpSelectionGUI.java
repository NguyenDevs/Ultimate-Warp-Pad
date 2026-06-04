package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.nguyendevs.ultimateWarpPad.flag.UWPFlags;
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
import org.nguyendevs.ultimateWarpPad.util.AbstractGUI;

import java.util.*;
import java.util.stream.Collectors;

public class WarpSelectionGUI {
    private static final int ITEMS_PER_PAGE = 18;
    private static final int SLOT_PREV = 21;
    private static final int SLOT_BEACON = 22;
    private static final int SLOT_NEXT = 23;
    private static final int SLOT_SETTINGS = 26;
    private static final int INVENTORY_SIZE = 27;

    private final JavaPlugin plugin;
    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final AnimationManager animationManager;

    private final Map<UUID, Long> cooldowns;
    private final TravelQueue travelQueue;
    private final SettingsGUI settingsGUI;

    public WarpSelectionGUI(JavaPlugin plugin,
                            WarpManager warpManager,
                            MessageManager messageManager,
                            ConfigManager configManager,
                            AnimationManager animationManager,
                            TravelQueue travelQueue,
                            SettingsGUI settingsGUI) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.animationManager = animationManager;
        this.cooldowns = new HashMap<>();
        this.travelQueue = travelQueue;
        this.settingsGUI = settingsGUI;
    }

    public void updateCooldown(UUID uuid) {
        cooldowns.put(uuid, System.currentTimeMillis());
    }

    public void open(Player player, Warp sourceWarp) {
        new GUI(player.getUniqueId(), sourceWarp).open(player);
    }

    @NotNull
    private ItemStack createSimpleItem(@NotNull Material mat,
                                       @NotNull String namePath) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get(namePath).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    private ItemStack createBeaconItem(@NotNull Warp sourceWarp, int mode) {
        boolean isAdmin = sourceWarp.isAdminWarp();

        List<String> modeNames = messageManager.getRawList(isAdmin
                ? "gui.warp_selection.filter_modes_admin"
                : "gui.warp_selection.filter_modes_player");

        Material mat = getModeIcon(isAdmin, mode);
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String modeName = mode < modeNames.size() ? modeNames.get(mode) : "Unknown";
        meta.displayName(messageManager.get(isAdmin
                        ? "gui.warp_selection.beacon.admin_name"
                        : "gui.warp_selection.beacon.player_name")
                .decoration(TextDecoration.ITALIC, false));
        boolean canSwitch = isAdmin || configManager.isConnectPrivateTrusted();
        String lorePath = isAdmin ? "gui.warp_selection.beacon.admin_lore"
                : (canSwitch ? "gui.warp_selection.beacon.player_lore"
                        : "gui.warp_selection.beacon.player_locked_lore");
        meta.lore(messageManager.getComponentList(lorePath,
                        Map.of("mode", modeName)).stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .collect(Collectors.toList()));

        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    private Material getModeIcon(boolean isAdmin, int mode) {
        if (isAdmin) {
            return switch (mode) {
                case 0 -> configManager.getDefaultAdminWarpIcon();
                case 1 -> configManager.getDefaultPlayerWarpIcon();
                default -> configManager.getDefaultPublicWarpIcon();
            };
        }
        return switch (mode) {
            case 0 -> configManager.getDefaultPlayerWarpIcon();
            default -> configManager.getDefaultPublicWarpIcon();
        };
    }

    @NotNull
    private List<Warp> getFilteredDestinations(@NotNull UUID playerUUID,
                                               @NotNull Warp sourceWarp,
                                               int mode) {
        if (sourceWarp.isAdminWarp()) {
            List<Warp> all = warpManager.getAvailableDestinations(sourceWarp, playerUUID);

            return switch (mode) {
                case 0 -> all.stream().filter(w -> w.getType() == WarpType.ADMIN).collect(Collectors.toList());
                case 1 -> all.stream().filter(w -> w.getType() == WarpType.PLAYER && w.isOwner(playerUUID))
                        .collect(Collectors.toList());
                case 2 -> all.stream().filter(w -> w.getType() == WarpType.PLAYER && w.isPublic())
                        .collect(Collectors.toList());
                default -> all;
            };
        }

        boolean connect = configManager.isConnectPrivateTrusted();

        if (!connect && !sourceWarp.isAdminWarp()) {
            if (sourceWarp.isOwner(playerUUID)) {
                return warpManager.getAvailableDestinations(sourceWarp, playerUUID);
            }
            if (sourceWarp.canPlayerUse(playerUUID)) {
                UUID owner = sourceWarp.getOwner();
                return warpManager.getAllWarps().stream()
                        .filter(w -> w.getOwner() != null && w.getOwner().equals(owner))
                        .filter(w -> w.canPlayerUse(playerUUID))
                        .filter(w -> !w.getCompositeId().equals(sourceWarp.getCompositeId()))
                        .filter(w -> warpManager.isInRange(sourceWarp, w))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        if (mode == 0) {
            if (sourceWarp.isOwner(playerUUID)) {
                return warpManager.getAvailableDestinations(sourceWarp, playerUUID);
            }
            if (connect && sourceWarp.canPlayerUse(playerUUID)) {
                return warpManager.getPlayerWarps(playerUUID).stream()
                        .filter(w -> !w.getCompositeId().equals(sourceWarp.getCompositeId()))
                        .filter(w -> warpManager.isInRange(sourceWarp, w))
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }

        if (sourceWarp.canPlayerUse(playerUUID) && !sourceWarp.isOwner(playerUUID)) {
            UUID owner = sourceWarp.getOwner();
            return warpManager.getAllWarps().stream()
                    .filter(w -> w.getOwner() != null && w.getOwner().equals(owner))
                    .filter(w -> w.canPlayerUse(playerUUID))
                    .filter(w -> !w.getCompositeId().equals(sourceWarp.getCompositeId()))
                    .filter(w -> warpManager.isInRange(sourceWarp, w))
                    .collect(Collectors.toList());
        }
        if (connect && sourceWarp.isOwner(playerUUID)) {
            return warpManager.getAllWarps().stream()
                    .filter(w -> w.getOwner() != null && !w.getOwner().equals(playerUUID))
                    .filter(w -> w.canPlayerUse(playerUUID))
                    .filter(w -> warpManager.isInRange(sourceWarp, w))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @NotNull
    private ItemStack createWarpItem(@NotNull Warp warp, @NotNull Warp sourceWarp) {
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

    @NotNull
    private ItemStack createSettingsShortcutItem(@NotNull Warp warp) {
        ItemStack item = new ItemStack(Material.PRISMARINE_CRYSTALS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.warp_selection.settings_shortcut.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.warp_selection.settings_shortcut.warp_name",
                        Map.of("name", warp.getWarpName()))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.warp_selection.settings_shortcut.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void startTravel(Player player, Warp source, Warp destination) {
        if (!UWPFlags.checkFlag(source.getLocation(), UWPFlags.UWP_USE)) {
            messageManager.send(player, "error.use_denied");
            travelQueue.onComplete(destination);
            return;
        }

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
        configManager.playSounds(sourceLoc.getWorld(), sourceLoc, configManager.getTravelStartSounds());

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

            if (configManager.isGroupCollision()) {
                member.setCollidable(false);
            }

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

    private class GUI extends AbstractGUI {
        private final UUID playerUUID;
        private final Warp sourceWarp;

        private int currentPage;
        private int mode;

        public GUI(UUID playerUUID, Warp sourceWarp) {
            super(INVENTORY_SIZE, messageManager.get("gui.warp_selection.title"));
            this.playerUUID = playerUUID;
            this.sourceWarp = sourceWarp;
            this.currentPage = 0;

            boolean canSwitch = sourceWarp.isAdminWarp() || configManager.isConnectPrivateTrusted();
            this.mode = (!canSwitch && !sourceWarp.isOwner(playerUUID)) ? 1 : 0;

            List<Warp> warps = getFilteredDestinations(playerUUID, sourceWarp, mode);
            int totalPages = (warps.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
            fillWarps(warps, totalPages);

            inventory.setItem(SLOT_BEACON, createBeaconItem(sourceWarp, mode));
            if (sourceWarp.isOwner(playerUUID)) {
                inventory.setItem(SLOT_SETTINGS, createSettingsShortcutItem(sourceWarp));
            }
        }

        @Override
        public void handleClick(@NotNull InventoryClickEvent event) {
            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

            switch (slot) {
                case SLOT_BEACON -> {
                    boolean canSwitch = sourceWarp.isAdminWarp() || configManager.isConnectPrivateTrusted();
                    if (!canSwitch) {
                        messageManager.send(player, "gui.warp_selection.cross_navigation_disabled");
                        player.playSound(player.getLocation(), "minecraft:block.note_block.bass",
                                SoundCategory.AMBIENT, 1.0f, 0.5f);
                        return;
                    }
                    int maxMode = sourceWarp.isAdminWarp() ? 3 : 2;
                    mode = (mode + 1) % maxMode;
                    rebuildWarps();
                }
                case SLOT_PREV -> {
                    if (currentPage <= 0)
                        return;
                    currentPage--;
                    rebuildWarps();
                }
                case SLOT_NEXT -> {
                    List<Warp> warps = getFilteredDestinations(playerUUID, sourceWarp, mode);
                    int totalPages = (warps.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
                    if (currentPage >= totalPages - 1)
                        return;
                    currentPage++;
                    rebuildWarps();
                }
                case SLOT_SETTINGS -> {
                    if (!sourceWarp.isOwner(playerUUID))
                        return;
                    settingsGUI.open(player, sourceWarp, true);
                }
                default -> {
                    if (slot >= ITEMS_PER_PAGE)
                        return;

                    List<Warp> destinations = getFilteredDestinations(playerUUID, sourceWarp, mode);
                    int index = currentPage * ITEMS_PER_PAGE + slot;
                    if (index >= destinations.size()) {
                        rebuildWarps();
                        return;
                    }

                    Warp destination = destinations.get(index);
                    if (!travelQueue.tryClaim(destination)) {
                        travelQueue.enqueue(player, sourceWarp, destination);
                        messageManager.send(player, "gui.warp_selection.warp_busy");
                        rebuildWarps();
                        return;
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
                        return;
                    }

                    int cd = configManager.getCooldown();
                    if (cd > 0) {
                        long last = cooldowns.getOrDefault(player.getUniqueId(), 0L);
                        long elapsed = (System.currentTimeMillis() - last) / 1000;
                        if (elapsed < cd) {
                            travelQueue.onComplete(destination);
                            messageManager.sendTravel(player, "cooldown",
                                    Map.of("time", String.valueOf(cd - elapsed)));
                            return;
                        }
                    }

                    player.closeInventory();
                    startTravel(player, sourceWarp, destination);
                }
            }
        }

        private void rebuildWarps() {
            List<Warp> warps = getFilteredDestinations(playerUUID, sourceWarp, mode);
            int totalPages = (warps.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
            fillWarps(warps, totalPages);
            inventory.setItem(SLOT_BEACON, createBeaconItem(sourceWarp, mode));
        }

        private void fillWarps(@NotNull List<@NotNull Warp> warps,
                               int totalPages) {
            if (totalPages > 0 && currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }

            int start = currentPage * ITEMS_PER_PAGE;
            int end = Math.min(start + ITEMS_PER_PAGE, warps.size());
            for (int slot = 0; slot < ITEMS_PER_PAGE; slot++) {
                int index = start + slot;
                if (index < end) {
                    inventory.setItem(slot, createWarpItem(warps.get(index), sourceWarp));
                } else {
                    inventory.setItem(slot, null);
                }
            }

            if (totalPages > 1) {
                inventory.setItem(SLOT_PREV, createSimpleItem(Material.ARROW, "gui.warp_selection.page_previous"));
                inventory.setItem(SLOT_NEXT, createSimpleItem(Material.ARROW, "gui.warp_selection.page_next"));
            } else {
                inventory.setItem(SLOT_PREV, null);
                inventory.setItem(SLOT_NEXT, null);
            }
        }
    }
}
