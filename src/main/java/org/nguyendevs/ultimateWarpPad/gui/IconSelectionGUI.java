package org.nguyendevs.ultimateWarpPad.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.util.AbstractGUI;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class IconSelectionGUI {
    private static final int ITEMS_PER_PAGE = 18;
    private static final int SLOT_PREV = 21;
    private static final int SLOT_NEXT = 23;
    private static final int SLOT_RETURN = 22;
    private static final int INVENTORY_SIZE = 27;

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final ConfigManager configManager;
    private final SettingsGUI settingsGUI;

    public IconSelectionGUI(@NotNull WarpManager warpManager,
                            @NotNull MessageManager messageManager,
                            @NotNull ConfigManager configManager,
                            @NotNull SettingsGUI settingsGUI) {
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.configManager = configManager;
        this.settingsGUI = settingsGUI;
    }

    public void open(@NotNull Player player,
                     @NotNull Warp editingWarp) {
        open(player, editingWarp, false);
    }

    public void open(@NotNull Player player,
                     @NotNull Warp editingWarp,
                     boolean fromSettings) {
        new GUI(editingWarp, fromSettings).open(player);
    }

    //TODO cache this and invalidate on config reload
    @NotNull
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

    private void fillIcons(@NotNull Inventory inv,
                           @NotNull List<@NotNull Material> icons,
                           int page) {
        int start = page * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, icons.size());
        for (int slot = 0; slot < ITEMS_PER_PAGE; slot++) {
            int index = start + slot;
            if (index < end) {
                Material mat = icons.get(index);
                ItemStack item = new ItemStack(mat);
                ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(mat.name())
                        .decoration(TextDecoration.ITALIC, false));
                item.setItemMeta(meta);
                inv.setItem(slot, item);
            } else {
                inv.setItem(slot, null);
            }
        }
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


    private class GUI extends AbstractGUI {
        private final Warp editingWarp;
        private final boolean fromSettings;
        private int currentPage;

        //TODO invalidate on config reload?
        private final List<Material> icons;
        private final int totalPages;

        public GUI(@NotNull Warp editingWarp, boolean fromSettings) {
            super(INVENTORY_SIZE, messageManager.get("gui.icon_selection.title"));
            this.editingWarp = editingWarp;
            this.fromSettings = fromSettings;
            this.currentPage = 0;

            icons = getAvailableIcons();
            totalPages = (icons.size() + ITEMS_PER_PAGE - 1) / ITEMS_PER_PAGE;
            if (totalPages > 1) {
                inventory.setItem(SLOT_PREV, createSimpleItem(Material.ARROW, "gui.icon_selection.page_previous"));
                inventory.setItem(SLOT_NEXT, createSimpleItem(Material.ARROW, "gui.icon_selection.page_next"));
            }
            inventory.setItem(SLOT_RETURN, createReturnItem());

            fillIcons(inventory, icons, currentPage);
        }

        @Override
        public void handleClick(@NotNull InventoryClickEvent event) {
            int slot = event.getSlot();

            Player player = (Player) event.getWhoClicked();
            player.playSound(player.getLocation(), "minecraft:ui.button.click", SoundCategory.AMBIENT, 1.0f, 1.0f);

            switch (slot) {
                case SLOT_PREV -> {
                    if (currentPage <= 0)
                        return;

                    fillIcons(inventory, icons, --currentPage);
                }
                case SLOT_NEXT -> {
                    if (currentPage >= totalPages - 1)
                        return;

                    fillIcons(inventory, icons, ++currentPage);
                }
                case SLOT_RETURN -> settingsGUI.open(player, editingWarp, fromSettings);
                default -> {
                    if (slot >= ITEMS_PER_PAGE)
                        return;

                    int index = currentPage * ITEMS_PER_PAGE + slot;
                    if (index >= icons.size())
                        return;

                    editingWarp.setIcon(icons.get(index));
                    warpManager.saveWarp(editingWarp);
                    settingsGUI.open(player, editingWarp, fromSettings);
                }
            }
        }
    }
}
