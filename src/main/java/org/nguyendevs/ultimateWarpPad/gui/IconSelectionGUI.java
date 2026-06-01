package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;

import java.util.*;
import java.util.stream.Collectors;

public class IconSelectionGUI {

    private static final int ITEMS_PER_PAGE = 18;
    private static final int SLOT_PREV = 21;
    private static final int SLOT_INFO = 22;
    private static final int SLOT_NEXT = 23;
    private static final int SLOT_RETURN = 26;

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final SettingsGUI settingsGUI;
    private final Map<UUID, Warp> editingWarps;
    private final Map<UUID, Integer> currentPage;
    private final Map<UUID, Boolean> fromSettingsMap;

    public IconSelectionGUI(WarpManager warpManager, MessageManager messageManager,
                            ConfigManager configManager, SettingsGUI settingsGUI) {
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.settingsGUI = settingsGUI;
        this.editingWarps = new HashMap<>();
        this.currentPage = new HashMap<>();
        this.fromSettingsMap = new HashMap<>();
    }

    public void open(Player player, Warp warp) {
        open(player, warp, false);
    }

    public void open(Player player, Warp warp, boolean fromSettings) {
        editingWarps.put(player.getUniqueId(), warp);
        fromSettingsMap.put(player.getUniqueId(), fromSettings);
        currentPage.putIfAbsent(player.getUniqueId(), 0);

        List<Material> icons = getAvailableIcons();
        int page = currentPage.getOrDefault(player.getUniqueId(), 0);
        int totalPages = (icons.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;

        Inventory inv = Bukkit.createInventory(null, 27,
                messageManager.get("gui.icon_selection.title"));

        fillIcons(inv, icons, page);

        if (totalPages > 1) {
            inv.setItem(SLOT_PREV, createSimpleItem(Material.ARROW, "gui.icon_selection.page_previous"));
            inv.setItem(SLOT_NEXT, createSimpleItem(Material.ARROW, "gui.icon_selection.page_next"));
        }

        inv.setItem(SLOT_INFO, createSimpleItem(Material.BEACON, "gui.icon_selection.info"));
        inv.setItem(SLOT_RETURN, createReturnItem());

        player.openInventory(inv);
        player.playSound(player.getLocation(), "minecraft:block.amethyst_block.resonate", SoundCategory.AMBIENT, 1.0f, 1.0f);
    }

    public void refresh(Player player) {
        Warp warp = editingWarps.get(player.getUniqueId());
        if (warp == null) return;
        open(player, warp);
    }

    public boolean handleClick(Player player, int slot) {
        Warp warp = editingWarps.get(player.getUniqueId());
        if (warp == null) return false;

        player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

        List<Material> icons = getAvailableIcons();
        int totalPages = (icons.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
        int page = currentPage.getOrDefault(player.getUniqueId(), 0);

        if (slot == SLOT_PREV) {
            if (page > 0) {
                currentPage.put(player.getUniqueId(), page - 1);
                refresh(player);
            }
            return true;
        }

        if (slot == SLOT_NEXT) {
            if (page < totalPages - 1) {
                currentPage.put(player.getUniqueId(), page + 1);
                refresh(player);
            }
            return true;
        }

        if (slot == SLOT_INFO) return true;

        if (slot == SLOT_RETURN) {
            Warp w = editingWarps.get(player.getUniqueId());
            boolean fromSettings = fromSettingsMap.getOrDefault(player.getUniqueId(), false);
            editingWarps.remove(player.getUniqueId());
            currentPage.remove(player.getUniqueId());
            fromSettingsMap.remove(player.getUniqueId());
            if (w != null) {
                settingsGUI.open(player, w, fromSettings);
            }
            return true;
        }

        if (slot >= 0 && slot < ITEMS_PER_PAGE) {
            int index = page * ITEMS_PER_PAGE + slot;
            if (index < icons.size()) {
                Material mat = icons.get(index);
                warp.setIcon(mat);
                warpManager.saveWarp(warp);
                boolean fromSettings = fromSettingsMap.getOrDefault(player.getUniqueId(), false);
                editingWarps.remove(player.getUniqueId());
                currentPage.remove(player.getUniqueId());
                fromSettingsMap.remove(player.getUniqueId());
                settingsGUI.open(player, warp, fromSettings);
            }
            return true;
        }

        return true;
    }

    private List<Material> getAvailableIcons() {
        return configManager.getWaypointIcons().stream()
                .map(name -> {
                    try {
                        return Material.valueOf(name.toUpperCase());
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void fillIcons(Inventory inv, List<Material> icons, int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, icons.size());
        for (int i = start; i < end; i++) {
            ItemStack item = new ItemStack(icons.get(i));
            ItemMeta meta = item.getItemMeta();
            meta.displayName(Component.text(icons.get(i).name())
                    .decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(meta);
            inv.setItem(i - start, item);
        }
    }

    private ItemStack createSimpleItem(Material mat, String namePath) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get(namePath).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack createReturnItem() {
        ItemStack item = new ItemStack(Material.STRUCTURE_VOID);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(messageManager.get("gui.icon_selection.return_button.name")
                .decoration(TextDecoration.ITALIC, false));
        List<Component> lore = messageManager.getComponentList("gui.icon_selection.return_button.lore").stream()
                .map(c -> c.decoration(TextDecoration.ITALIC, false))
                .toList();
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isOpen(Player player) {
        return editingWarps.containsKey(player.getUniqueId());
    }

    public void cleanup(Player player) {
        editingWarps.remove(player.getUniqueId());
        currentPage.remove(player.getUniqueId());
        fromSettingsMap.remove(player.getUniqueId());
    }
}
