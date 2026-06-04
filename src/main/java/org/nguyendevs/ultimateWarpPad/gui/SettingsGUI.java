package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.CostType;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;
import org.nguyendevs.ultimateWarpPad.util.AbstractGUI;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SettingsGUI {

    private static final int SLOT_ITEM1 = 9;
    private static final int SLOT_ITEM2 = 11;
    private static final int SLOT_ITEM4 = 13;
    private static final int SLOT_ICON = 15;
    private static final int SLOT_DELETE = 17;
    private static final int SLOT_RETURN_CLOSE = 22;
    private static final int INVENTORY_SIZE = 27;

    private static final int[] ADMIN_RANGES = {-1, 1000, 1500, 2000, 2500, 3000, 3500, 4000, 4500, 5000};
    private static final int[] PLAYER_RANGES = {50, 100, 250, 500, 1000, 2000, 3000, 4000, 5000};

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final Map<UUID, PendingData> pendingNameChanges;
    private final Map<UUID, PendingData> pendingCostAmounts;
    private final Map<UUID, PendingData> pendingDeletions;

    private IconSelectionGUI iconSelectionGUI;
    private WarpSelectionGUI warpSelectionGUI;

    public SettingsGUI(WarpManager warpManager, MessageManager messageManager, ConfigManager configManager) {
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.pendingNameChanges = new ConcurrentHashMap<>();
        this.pendingCostAmounts = new ConcurrentHashMap<>();
        this.pendingDeletions = new ConcurrentHashMap<>();
    }

    public void setIconSelectionGUI(IconSelectionGUI iconSelectionGUI) {
        this.iconSelectionGUI = iconSelectionGUI;
    }

    public void setWarpSelectionGUI(WarpSelectionGUI warpSelectionGUI) {
        this.warpSelectionGUI = warpSelectionGUI;
    }

    public void open(Player player, Warp warp) {
        open(player, warp, false);
    }

    public void open(Player player, Warp warp, boolean fromSelection) {
        new GUI(player, warp, fromSelection).open(player);
    }

    public void open(Player player, PendingData data) {
        new GUI(player, data.warp, data.fromSelection).open(player);
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
                Map.of("name", warp.getWarpName()))
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
        int range = getPermissionRange(player);
        boolean isExtra = range > 0 && warp.getRange() == range;
        String rangeText = warp.getRange() < 0
                ? messageManager.getRaw("gui.settings.range.unlimited")
                : warp.getRange() + " blocks" + (isExtra ? " &6(Extra)" : "");
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

    private ItemStack createReturnItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.return_button.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = messageManager.getComponentList("gui.settings.return_button.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList();
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createCloseItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.settings.close_button.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = messageManager.getComponentList("gui.settings.close_button.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList();
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void handleVisibilityToggle(Player player, GUI gui) {
        Warp warp = gui.warp;
        boolean nowPublic = !warp.isPublic();
        warp.setPublic(nowPublic);
        warp.setIcon(nowPublic
                ? configManager.getDefaultPublicWarpIcon()
                : configManager.getDefaultPlayerWarpIcon());
        warpManager.saveWarp(warp);
        messageManager.send(player, "warp.visibility_changed",
                Map.of("visibility", nowPublic ? "Public" : "Private"));
        gui.getInventory().setItem(SLOT_ITEM1, createVisibilityItem(warp));
        gui.getInventory().setItem(SLOT_ICON, createIconItem(warp));
    }

    private void handleNameChange(Player player, GUI gui) {
        player.closeInventory();
        pendingNameChanges.put(player.getUniqueId(), new PendingData(gui.warp, gui.fromSelection));
        messageManager.send(player, "prompt.enter_name");
    }

    private void handleCostTypeCycle(GUI gui) {
        Warp warp = gui.warp;
        CostType[] types = CostType.values();
        int next = (warp.getCostType().ordinal() + 1) % types.length;
        warp.setCostType(types[next]);
        if (types[next] == CostType.FREE) {
            warp.setCost(-1);
        }
        warpManager.saveWarp(warp);
        gui.getInventory().setItem(SLOT_ITEM2, createCostItem(warp));
    }

    private void handleCostAmountPrompt(Player player, GUI gui) {
        if (gui.warp.getCostType() == CostType.FREE) return;
        player.closeInventory();
        pendingCostAmounts.put(player.getUniqueId(), new PendingData(gui.warp, gui.fromSelection));
        messageManager.send(player, "prompt.enter_cost_amount");
    }

    private void handleRangeCycle(Player player, GUI gui) {
        Warp warp = gui.warp;
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
        gui.getInventory().setItem(SLOT_ITEM4, createRangeItem(warp, player));
    }

    private void handleIconSelection(Player player, GUI gui) {
        iconSelectionGUI.open(player, gui.warp, gui.fromSelection);
    }

    private void handleDeletePrompt(Player player, GUI gui) {
        player.closeInventory();
        pendingDeletions.put(player.getUniqueId(), new PendingData(gui.warp, gui.fromSelection));
        messageManager.send(player, "prompt.delete_confirm");
    }

    private int getPermissionRange(Player player) {
        for (int i = 10000; i >= 1; i--) {
            if (player.hasPermission("uwp.user.range." + i)) {
                return i;
            }
        }
        return -1;
    }

    private int[] getAvailableRanges(Warp warp, Player player) {
        if (warp.isAdminWarp()) return ADMIN_RANGES;
        int permRange = getPermissionRange(player);
        if (permRange > 0) {
            int[] base = PLAYER_RANGES;
            int[] result = Arrays.copyOf(base, base.length + 1);
            result[result.length - 1] = permRange;
            return result;
        }
        return PLAYER_RANGES;
    }

    public void close(Player player) {
    }

    public PendingData removePendingNameChange(UUID playerUUID) {
        return pendingNameChanges.remove(playerUUID);
    }

    public boolean hasPendingNameChange(UUID playerUUID) {
        return pendingNameChanges.containsKey(playerUUID);
    }

    public PendingData removePendingCostAmount(UUID playerUUID) {
        return pendingCostAmounts.remove(playerUUID);
    }

    public boolean hasPendingCostAmount(UUID playerUUID) {
        return pendingCostAmounts.containsKey(playerUUID);
    }

    public PendingData getPendingDeletion(UUID playerUUID) {
        return pendingDeletions.get(playerUUID);
    }

    public PendingData removePendingDeletion(UUID playerUUID) {
        return pendingDeletions.remove(playerUUID);
    }

    public boolean hasPendingDeletion(UUID playerUUID) {
        return pendingDeletions.containsKey(playerUUID);
    }

    public void cleanup(Player player) {
        UUID uuid = player.getUniqueId();
        pendingNameChanges.remove(uuid);
        pendingCostAmounts.remove(uuid);
        pendingDeletions.remove(uuid);
    }

    public record PendingData(Warp warp, boolean fromSelection) {
    }

    private class GUI extends AbstractGUI {
        private final Warp warp;
        private final boolean fromSelection;

        public GUI(Player player, Warp warp, boolean fromSelection) {
            super(INVENTORY_SIZE, messageManager.get("gui.settings.title"));
            this.warp = warp;
            this.fromSelection = fromSelection;

            if (warp.getType() == WarpType.PLAYER) {
                inventory.setItem(SLOT_ITEM1, createVisibilityItem(warp));
                inventory.setItem(SLOT_ITEM2, createNameItem(warp));
                inventory.setItem(SLOT_ITEM4, createRangeItem(warp, player));
            } else {
                inventory.setItem(SLOT_ITEM1, createNameItem(warp));
                inventory.setItem(SLOT_ITEM2, createCostItem(warp));
                inventory.setItem(SLOT_ITEM4, createRangeItem(warp, player));
            }

            inventory.setItem(SLOT_ICON, createIconItem(warp));
            inventory.setItem(SLOT_DELETE, createDeleteItem(warp));

            if (fromSelection) {
                inventory.setItem(SLOT_RETURN_CLOSE, createReturnItem());
            } else {
                inventory.setItem(SLOT_RETURN_CLOSE, createCloseItem());
            }
        }

        @Override
        public void handleClick(@NotNull InventoryClickEvent event) {
            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

            int slot = event.getSlot();
            switch (slot) {
                case SLOT_RETURN_CLOSE -> {
                    if (fromSelection) {
                        warpSelectionGUI.open(player, warp);
                        return;
                    }
                    player.closeInventory();
                }
                case SLOT_DELETE -> handleDeletePrompt(player, this);
                case SLOT_ICON -> handleIconSelection(player, this);
                case SLOT_ITEM1 -> {
                    if (warp.getType() == WarpType.PLAYER) {
                        handleVisibilityToggle(player, this);
                        return;
                    }
                    handleNameChange(player, this);
                }
                case SLOT_ITEM2 -> {
                    if (warp.getType() == WarpType.PLAYER) {
                        handleNameChange(player, this);
                        return;
                    }

                    if (event.isRightClick()) {
                        handleCostAmountPrompt(player, this);
                    } else {
                        handleCostTypeCycle(this);
                    }
                }
                case SLOT_ITEM4 -> handleRangeCycle(player, this);
            }
        }
    }
}
