package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsGUI {

    private static final int SLOT_ITEM1 = 0;
    private static final int SLOT_ITEM2 = 2;
    private static final int SLOT_ITEM3 = 3;
    private static final int SLOT_ITEM4 = 4;
    private static final int SLOT_ICON = 6;
    private static final int SLOT_DELETE = 8;

    private static final int[] ADMIN_RANGES = {-1, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000};
    private static final int[] PLAYER_RANGES = {50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000};

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final Map<UUID, Warp> openSettings;
    private final Map<UUID, Warp> pendingNameChanges;
    private final Map<UUID, Warp> pendingCostAmounts;
    private final Map<UUID, Warp> pendingDeletions;
    private IconSelectionGUI iconSelectionGUI;

    public SettingsGUI(WarpManager warpManager, MessageManager messageManager) {
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.openSettings = new HashMap<>();
        this.pendingNameChanges = new ConcurrentHashMap<>();
        this.pendingCostAmounts = new ConcurrentHashMap<>();
        this.pendingDeletions = new ConcurrentHashMap<>();
    }

    public void setIconSelectionGUI(IconSelectionGUI iconSelectionGUI) {
        this.iconSelectionGUI = iconSelectionGUI;
    }

    public void open(Player player, Warp warp) {
        Inventory inv = Bukkit.createInventory(null, 9,
                messageManager.get("gui.settings.title"));

        if (warp.getType() == WarpType.PLAYER) {
            inv.setItem(SLOT_ITEM1, createVisibilityItem(warp));
            inv.setItem(SLOT_ITEM2, createNameItem(warp));
            inv.setItem(SLOT_ITEM4, createRangeItem(warp, player));
        } else {
            inv.setItem(SLOT_ITEM1, createNameItem(warp));
            inv.setItem(SLOT_ITEM2, createCostItem(warp));
            inv.setItem(SLOT_ITEM4, createRangeItem(warp, player));
        }

        inv.setItem(SLOT_ICON, createIconItem(warp));
        inv.setItem(SLOT_DELETE, createDeleteItem(warp));

        player.openInventory(inv);
        player.playSound(player.getLocation(), "minecraft:block.amethyst_block.resonate", SoundCategory.AMBIENT, 1.0f, 1.0f);
        openSettings.put(player.getUniqueId(), warp);
    }

    public boolean handleClick(Player player, int slot, ClickType clickType, ItemStack cursor) {
        Warp warp = openSettings.get(player.getUniqueId());
        if (warp == null) return false;

        player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

        if (slot == SLOT_DELETE) {
            handleDeletePrompt(player, warp);
            return true;
        }

        if (slot == SLOT_ICON) {
            handleIconSelection(player, warp);
            return true;
        }

        if (warp.getType() == WarpType.PLAYER) {
            switch (slot) {
                case SLOT_ITEM1 -> handleVisibilityToggle(player, warp);
                case SLOT_ITEM2 -> handleNameChange(player, warp);
                case SLOT_ITEM4 -> handleRangeCycle(player, warp);
                default -> { return false; }
            }
        } else {
            switch (slot) {
                case SLOT_ITEM1 -> handleNameChange(player, warp);
                case SLOT_ITEM2 -> {
                    if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
                        handleCostAmountPrompt(player, warp);
                    } else {
                        handleCostTypeCycle(player, warp);
                    }
                }
                case SLOT_ITEM4 -> handleRangeCycle(player, warp);
                default -> { return false; }
            }
        }
        return true;
    }

    public boolean handleDrag(Player player, int slot, ItemStack dropped) {
        return false;
    }

    private ItemStack createVisibilityItem(Warp warp) {
        ItemStack item = new ItemStack(warp.isPublic() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.public_toggle.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.settings.public_toggle." + (warp.isPublic() ? "public" : "private"))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.settings.public_toggle.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createNameItem(Warp warp) {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.name.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.settings.name.current",
                Map.of("name", messageManager.translateColorCodes(warp.getWarpName())))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.settings.name.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCostItem(Warp warp) {
        Material mat = switch (warp.getCostType()) {
            case FREE -> Material.BARRIER;
            case XP -> Material.EXPERIENCE_BOTTLE;
            case MONEY -> Material.EMERALD;
        };
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.cost.name")
                .decoration(TextDecoration.ITALIC, false));
        String amountText = warp.getCost() < 0
                ? messageManager.getRaw("gui.settings.cost.free")
                : String.valueOf((int) warp.getCost());
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.settings.cost.type_line",
                Map.of("type", warp.getCostType().name()))
                .decoration(TextDecoration.ITALIC, false));
        lore.add(messageManager.get("gui.settings.cost.amount_line",
                Map.of("amount", amountText))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.settings.cost.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createRangeItem(Warp warp, Player player) {
        ItemStack item = new ItemStack(Material.COMPASS);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.range.name")
                .decoration(TextDecoration.ITALIC, false));
        String rangeText = warp.getRange() < 0
                ? messageManager.getRaw("gui.settings.range.unlimited")
                : warp.getRange() + " blocks";
        List<Component> lore = new ArrayList<>();
        lore.add(messageManager.get("gui.settings.range.current",
                Map.of("range", rangeText))
                .decoration(TextDecoration.ITALIC, false));
        lore.addAll(messageManager.getComponentList("gui.settings.range.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createIconItem(Warp warp) {
        ItemStack item = new ItemStack(warp.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.icon.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.addAll(messageManager.getComponentList("gui.settings.icon.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createDeleteItem(Warp warp) {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.delete.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = new ArrayList<>();
        lore.addAll(messageManager.getComponentList("gui.settings.delete.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList());
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void handleVisibilityToggle(Player player, Warp warp) {
        warp.setPublic(!warp.isPublic());
        warpManager.saveWarp(warp);
        messageManager.send(player, "warp.visibility_changed",
                Map.of("visibility", warp.isPublic() ? "Public" : "Private"));
        open(player, warp);
    }

    private void handleNameChange(Player player, Warp warp) {
        player.closeInventory();
        openSettings.remove(player.getUniqueId());
        pendingNameChanges.put(player.getUniqueId(), warp);
        messageManager.send(player, "prompt.enter_name");
    }

    private void handleCostTypeCycle(Player player, Warp warp) {
        CostType[] types = CostType.values();
        int next = (warp.getCostType().ordinal() + 1) % types.length;
        warp.setCostType(types[next]);
        if (types[next] == CostType.FREE) {
            warp.setCost(-1);
        }
        warpManager.saveWarp(warp);
        open(player, warp);
    }

    private void handleCostAmountPrompt(Player player, Warp warp) {
        if (warp.getCostType() == CostType.FREE) return;
        player.closeInventory();
        openSettings.remove(player.getUniqueId());
        pendingCostAmounts.put(player.getUniqueId(), warp);
        messageManager.send(player, "prompt.enter_cost_amount");
    }

    private void handleRangeCycle(Player player, Warp warp) {
        int[] ranges = getAvailableRanges(warp, player);
        int current = warp.getRange();
        int nextIndex = 0;
        for (int i = 0; i < ranges.length; i++) {
            if (ranges[i] == current) {
                nextIndex = (i + 1) % ranges.length;
                break;
            }
        }
        int newRange = ranges[nextIndex];
        warp.setRange(newRange);
        warpManager.saveWarp(warp);
        String rangeText = newRange < 0
                ? messageManager.getRaw("gui.settings.range.unlimited")
                : newRange + " blocks";
        messageManager.send(player, "warp.range_changed", Map.of("range", rangeText));
        open(player, warp);
    }

    private void handleIconSelection(Player player, Warp warp) {
        if (iconSelectionGUI != null) {
            iconSelectionGUI.open(player, warp);
        }
    }

    private void handleDeletePrompt(Player player, Warp warp) {
        player.closeInventory();
        openSettings.remove(player.getUniqueId());
        pendingDeletions.put(player.getUniqueId(), warp);
        messageManager.send(player, "prompt.delete_confirm");
    }

    private int[] getAvailableRanges(Warp warp, Player player) {
        if (warp.isAdminWarp()) return ADMIN_RANGES;
        return PLAYER_RANGES;
    }

    public void close(Player player) {
        openSettings.remove(player.getUniqueId());
    }

    public Warp removePendingNameChange(UUID playerUUID) {
        return pendingNameChanges.remove(playerUUID);
    }

    public boolean hasPendingNameChange(UUID playerUUID) {
        return pendingNameChanges.containsKey(playerUUID);
    }

    public Warp removePendingCostAmount(UUID playerUUID) {
        return pendingCostAmounts.remove(playerUUID);
    }

    public boolean hasPendingCostAmount(UUID playerUUID) {
        return pendingCostAmounts.containsKey(playerUUID);
    }

    public Warp getPendingDeletion(UUID playerUUID) {
        return pendingDeletions.get(playerUUID);
    }

    public Warp removePendingDeletion(UUID playerUUID) {
        return pendingDeletions.remove(playerUUID);
    }

    public boolean hasPendingDeletion(UUID playerUUID) {
        return pendingDeletions.containsKey(playerUUID);
    }

    public void cleanup(Player player) {
        openSettings.remove(player.getUniqueId());
        pendingNameChanges.remove(player.getUniqueId());
        pendingCostAmounts.remove(player.getUniqueId());
        pendingDeletions.remove(player.getUniqueId());
    }
}
