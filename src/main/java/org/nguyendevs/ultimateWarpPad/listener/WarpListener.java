package org.nguyendevs.ultimateWarpPad.listener;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.nguyendevs.ultimateWarpPad.UltimateWarpPad;
import org.nguyendevs.ultimateWarpPad.gui.IconSelectionGUI;
import org.nguyendevs.ultimateWarpPad.gui.SettingsGUI;
import org.nguyendevs.ultimateWarpPad.gui.WarpSelectionGUI;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;

import java.util.*;

public class WarpListener implements Listener {

    private final UltimateWarpPad plugin;
    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final WarpSelectionGUI warpSelectionGUI;
    private final SettingsGUI settingsGUI;
    private final IconSelectionGUI iconSelectionGUI;

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
