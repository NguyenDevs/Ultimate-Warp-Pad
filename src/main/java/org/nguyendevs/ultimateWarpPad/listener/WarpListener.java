package org.nguyendevs.ultimateWarpPad.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.nguyendevs.ultimateWarpPad.UltimateWarpPad;
import org.nguyendevs.ultimateWarpPad.gui.IconSelectionGUI;
import org.nguyendevs.ultimateWarpPad.gui.SettingsGUI;
import org.nguyendevs.ultimateWarpPad.gui.WarpSelectionGUI;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.CraftManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;

import java.util.*;

public class WarpListener implements Listener {

    private final UltimateWarpPad plugin;
    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final WarpSelectionGUI warpSelectionGUI;
    private final SettingsGUI settingsGUI;
    private final IconSelectionGUI iconSelectionGUI;
    private CraftManager craftManager;

    private final Map<UUID, Warp> playerOnWarp;

    public WarpListener(UltimateWarpPad plugin, WarpManager warpManager, MessageManager messageManager,
                        ConfigManager configManager, WarpSelectionGUI warpSelectionGUI,
                        SettingsGUI settingsGUI, IconSelectionGUI iconSelectionGUI) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.warpSelectionGUI = warpSelectionGUI;
        this.settingsGUI = settingsGUI;
        this.iconSelectionGUI = iconSelectionGUI;
        this.playerOnWarp = new HashMap<>();
    }

    public void setCraftManager(CraftManager craftManager) {
        this.craftManager = craftManager;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCraftItem(CraftItemEvent event) {
        if (craftManager == null || !craftManager.isEnabled()) return;

        ItemStack result = event.getRecipe().getResult();
        if (craftManager.isCraftItem(result)) {
            if (event.getWhoClicked() instanceof Player player) {
                if (!player.hasPermission(craftManager.getPermission())) {
                    event.setCancelled(true);
                    messageManager.send(player, "error.permission");
                    player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = event.getItemInHand();
        if (craftManager == null || !craftManager.isEnabled()) return;
        if (hand == null || hand.getItemMeta() == null) return;
        if (!hand.getItemMeta().getPersistentDataContainer().has(craftManager.getPdcKey(), PersistentDataType.BYTE))
            return;

        if (!player.hasPermission("uwp.user.create")) {
            event.setCancelled(true);
            messageManager.send(player, "error.permission");
            return;
        }

        if (configManager.isDisabledWorld(player.getWorld().getName())) {
            event.setCancelled(true);
            messageManager.send(player, "error.disabled_world");
            return;
        }

        Location loc = event.getBlock().getLocation();

        if (!warpManager.isSkyClear(loc)) {
            event.setCancelled(true);
            messageManager.send(player, "error.blocked_above");
            return;
        }

        if (warpManager.getOverlappingWarp(loc) != null) {
            event.setCancelled(true);
            messageManager.send(player, "warp.overlaps_existing");
            return;
        }

        UUID uuid = player.getUniqueId();
        int max = warpManager.getMaxWarpsPerPlayer();
        if (warpManager.getPlayerWarpCount(uuid) >= max) {
            event.setCancelled(true);
            messageManager.send(player, "warp.max_reached", Map.of("max", String.valueOf(max)));
            return;
        }

        String warpId = generateWarpId(uuid);
        String warpName = "Warp " + WarpManager.toRoman(warpManager.getPlayerWarpCount(uuid) + 1);

        hand.setAmount(hand.getAmount() - 1);

        org.bukkit.block.Block block = event.getBlock();
        org.bukkit.Material placedType = event.getBlockPlaced().getType();

        org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (block.getType() == placedType) {
                block.setType(org.bukkit.Material.AIR);
            }

            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_GRINDSTONE_USE, 1.0f, 0.8f);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_AMETHYST_BLOCK_HIT, 1.0f, 0.5f);
            player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, 1.0f, 0.5f);

            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Warp warp = new Warp();
                warp.setOwner(uuid);
                warp.setWarpId(warpId);
                warp.setWarpName(warpName);
                warp.setLocation(loc);
                warp.setType(WarpType.PLAYER);
                warp.setCostType(CostType.XP);
                warp.setCost(-1);
                warp.setRange(1000);

                if (warpManager.createWarp(warp)) {
                    messageManager.send(player, "warp.created", Map.of("name", warpName));
                }
            }, 20L);
        }, 1L);
    }

    private String generateWarpId(UUID owner) {
        int count = warpManager.getPlayerWarpCount(owner) + 1;
        String base = "warp" + count;
        String id = base;
        int suffix = 1;
        while (warpManager.isWarpIdTaken(owner, id)) {
            id = base + suffix++;
        }
        return id;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location to = event.getTo();
        if (to == null) return;

        if (event.getFrom().getBlockX() == to.getBlockX()
                && event.getFrom().getBlockY() == to.getBlockY()
                && event.getFrom().getBlockZ() == to.getBlockZ()) {
            return;
        }

        Warp warp = warpManager.getWarpAtLocation(to);
        if (warp != null && warp.canPlayerUse(player.getUniqueId())) {
            playerOnWarp.put(player.getUniqueId(), warp);
        } else {
            playerOnWarp.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;
        Player player = event.getPlayer();
        if (configManager.isDisabledWorld(player.getWorld().getName())) return;
        Warp warp = playerOnWarp.get(player.getUniqueId());
        if (warp != null) {
            warpSelectionGUI.open(player, warp);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Component title = event.getView().title();
        Component settingsTitle = messageManager.get("gui.settings.title");
        Component selectionTitle = messageManager.get("gui.warp_selection.title");
        Component iconTitle = messageManager.get("gui.icon_selection.title");

        if (title.equals(settingsTitle)) {
            event.setCancelled(true);

            if (event.getSlot() >= 0 && event.getCurrentItem() != null) {
                settingsGUI.handleClick(player, event.getSlot(), event.getClick(), event.getCursor());
            }
            return;
        }

        if (title.equals(selectionTitle)) {
            event.setCancelled(true);

            if (event.getSlot() >= 0 && event.getCurrentItem() != null) {
                warpSelectionGUI.handleClick(player, event.getSlot());
            }
            return;
        }

        if (title.equals(iconTitle)) {
            event.setCancelled(true);

            if (event.getSlot() >= 0) {
                iconSelectionGUI.handleClick(player, event.getSlot());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Component title = event.getView().title();
        Component settingsTitle = messageManager.get("gui.settings.title");
        if (!title.equals(settingsTitle)) return;

        for (int slot : event.getRawSlots()) {
            if (slot >= 0 && slot < 9) {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        boolean hasNamePending = settingsGUI.hasPendingNameChange(player.getUniqueId());
        boolean hasCostPending = settingsGUI.hasPendingCostAmount(player.getUniqueId());
        boolean hasDeletePending = settingsGUI.hasPendingDeletion(player.getUniqueId());
        if (!hasNamePending && !hasCostPending && !hasDeletePending) return;

        event.setCancelled(true);
        String message = event.getMessage().trim();

        org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
            if (hasNamePending) {
                processNameChange(player, message);
            } else if (hasCostPending) {
                processCostAmount(player, message);
            } else if (hasDeletePending) {
                processDeleteConfirm(player, message);
            }
        });
    }

    private void processNameChange(Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            settingsGUI.removePendingNameChange(player.getUniqueId());
            messageManager.send(player, "prompt.enter_name_cancelled");
            return;
        }

        Warp warp = settingsGUI.removePendingNameChange(player.getUniqueId());
        if (warp == null) return;

        String newName = message.replace('&', '§');
        warp.setWarpName(newName);
        warpManager.saveWarp(warp);
        messageManager.send(player, "warp.name_changed", Map.of("name", newName));
        settingsGUI.open(player, warp);
    }

    private void processCostAmount(Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            settingsGUI.removePendingCostAmount(player.getUniqueId());
            messageManager.send(player, "prompt.enter_name_cancelled");
            return;
        }

        Warp warp = settingsGUI.removePendingCostAmount(player.getUniqueId());
        if (warp == null) return;

        try {
            int amount = Integer.parseInt(message);
            if (amount < 0) {
                messageManager.send(player, "prompt.invalid_cost_amount");
                return;
            }
            double oldCost = warp.getCost();
            warp.setCost(amount);
            warpManager.saveWarp(warp);
            messageManager.send(player, "warp.cost_changed",
                    Map.of("old", String.valueOf((int) oldCost),
                            "new", String.valueOf(amount)));
            settingsGUI.open(player, warp);
        } catch (NumberFormatException e) {
            messageManager.send(player, "prompt.invalid_cost_amount");
        }
    }

    private void processDeleteConfirm(Player player, String message) {
        if (message.equalsIgnoreCase("cancel")) {
            settingsGUI.removePendingDeletion(player.getUniqueId());
            messageManager.send(player, "prompt.delete_cancelled");
            return;
        }

        if (!message.equalsIgnoreCase("confirm")) {
            messageManager.send(player, "prompt.delete_confirm");
            return;
        }

        Warp warp = settingsGUI.removePendingDeletion(player.getUniqueId());
        if (warp == null) return;

        warpManager.deleteWarp(warp);
        messageManager.send(player, "warp.deleted", Map.of("id", warp.getWarpId()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFallDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player
                && event.getCause() == EntityDamageEvent.DamageCause.FALL
                && UltimateWarpPad.FALL_DAMAGE_IMMUNE.contains(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        playerOnWarp.remove(uuid);
        UltimateWarpPad.FALL_DAMAGE_IMMUNE.remove(uuid);
        warpSelectionGUI.cleanup(player);
        settingsGUI.cleanup(player);
        iconSelectionGUI.cleanup(player);
    }

    public void cleanup() {
        playerOnWarp.clear();
        UltimateWarpPad.FALL_DAMAGE_IMMUNE.clear();
    }
}
