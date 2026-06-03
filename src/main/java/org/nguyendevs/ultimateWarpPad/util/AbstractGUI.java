package org.nguyendevs.ultimateWarpPad.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractGUI implements InventoryHolder {
    protected final Inventory inventory;

    public AbstractGUI(int size, @NotNull Component title) {
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void open(@NotNull Player player) {
        if (player.openInventory(inventory) == null)
            return;
        player.playSound(player.getLocation(), "minecraft:block.amethyst_block.resonate", SoundCategory.AMBIENT, 1.0f, 1.0f);
    }

    @Override
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }

    public abstract void handleClick(@NotNull InventoryClickEvent event);
}
