package org.nguyendevs.ultimateWarpPad.manager;

import net.kyori.adventure.text.Component;
 import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftManager {

    private static final String RECIPE_KEY = "wpp_craft";
    public  static final String PDC_KEY     = "warp_creator";

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage;
    private NamespacedKey pdcKey;

    private boolean enabled;
    private boolean disableCommand;
    private boolean craftable;
    private boolean requirePermission;
    private String permission;
    private ItemStack craftItem;
    private String lastRecipeHash;

    public CraftManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.pdcKey = new NamespacedKey(plugin, PDC_KEY);
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "craft.yml");
        if (!file.exists()) {
            plugin.saveResource("craft.yml", false);
        }

        mergeDefaults(file);

        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);

        enabled = cfg.getBoolean("craft.enable", false);
        disableCommand = cfg.getBoolean("craft.disable-command", true);
        craftable = cfg.getBoolean("craft.craftable", true);
        requirePermission = cfg.getBoolean("craft.require-permission", false);
        permission = cfg.getString("craft.permission", "uwp.craft");

        craftItem = buildCraftItem(cfg);

        if (!enabled) {
            unregisterRecipe();
            lastRecipeHash = null;
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &cCraft system disabled."));
            return;
        }

        if (craftable) {
            String newHash = computeRecipeHash(cfg);
            if (newHash.equals(lastRecipeHash)) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aRecipe unchanged, skipping re-registration."));
                return;
            }
            unregisterRecipe();
            registerRecipe(cfg);
            lastRecipeHash = newHash;
        } else {
            unregisterRecipe();
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aCraftable is false, recipe removed."));
        }
    }

    private ItemStack buildCraftItem(YamlConfiguration cfg) {
        String matName = cfg.getString("item", "END_ROD");
        Material mat = Material.matchMaterial(matName);
        if (mat == null) {
            plugin.getLogger().warning("Unknown item material: " + matName + ", falling back to END_ROD.");
            mat = Material.END_ROD;
        }

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();

        String rawName = cfg.getString("name", "&aWarp Creator");
        meta.displayName(parseComponent("<!italic>" + rawName));

        List<String> rawLore = cfg.getStringList("lore");
        if (!rawLore.isEmpty()) {
            List<Component> lore = new ArrayList<>();
            for (String line : rawLore) {
                lore.add(parseComponent("<!italic>" + line));
            }
            meta.lore(lore);
        }

        meta.getPersistentDataContainer().set(pdcKey, PersistentDataType.BYTE, (byte) 1);

        item.setItemMeta(meta);
        return item;
    }

    private void registerRecipe(YamlConfiguration cfg) {
        List<String> shape = cfg.getStringList("craft.recipe");
        if (shape.isEmpty() || shape.size() > 3) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &cInvalid recipe shape in craft.yml (must be 1-3 rows)."));
            return;
        }

        for (int i = 0; i < shape.size(); i++) {
            if (shape.get(i).length() != 3) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aRecipe row " + (i + 1) + " must be exactly 3 characters."));
                return;
            }
        }

        Map<String, Object> rawMaterials = cfg.getConfigurationSection("craft.materials") != null
                ? cfg.getConfigurationSection("craft.materials").getValues(false)
                : Map.of();

        NamespacedKey key = new NamespacedKey(plugin, RECIPE_KEY);
        ShapedRecipe recipe = new ShapedRecipe(key, craftItem.clone());

        switch (shape.size()) {
            case 1 -> recipe.shape(shape.get(0));
            case 2 -> recipe.shape(shape.get(0), shape.get(1));
            default -> recipe.shape(shape.get(0), shape.get(1), shape.get(2));
        }

        for (Map.Entry<String, Object> entry : rawMaterials.entrySet()) {
            String symbol = entry.getKey();
            if (symbol.length() != 1 || symbol.charAt(0) == ' ') continue;
            Material mat = Material.matchMaterial(entry.getValue().toString());
            if (mat == null) {
                plugin.getLogger().warning("Unknown material for symbol '" + symbol + "': " + entry.getValue());
                continue;
            }
            recipe.setIngredient(symbol.charAt(0), mat);
        }

        Bukkit.addRecipe(recipe);
    }

    private void unregisterRecipe() {
        NamespacedKey key = new NamespacedKey(plugin, RECIPE_KEY);
        if (Bukkit.getRecipe(key) != null) {
            Bukkit.removeRecipe(key);
        }
    }

    private String computeRecipeHash(YamlConfiguration cfg) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(cfg.getStringList("craft.recipe"));
            if (cfg.getConfigurationSection("craft.materials") != null) {
                sb.append(cfg.getConfigurationSection("craft.materials").getValues(false));
            }
            sb.append(cfg.getString("item", ""));
            sb.append(cfg.getString("name", ""));
            sb.append(cfg.getStringList("lore"));

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to compute recipe hash: " + e.getMessage());
            return "";
        }
    }

    private void mergeDefaults(File file) {
        try (InputStream defStream = plugin.getResource("craft.yml")) {
            if (defStream == null)
                return;
            YamlConfiguration defCfg = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8));
            YamlConfiguration serverCfg = YamlConfiguration.loadConfiguration(file);

            boolean changed = false;
            for (String key : defCfg.getKeys(true)) {
                if (!defCfg.isConfigurationSection(key) && !serverCfg.contains(key)) {
                    serverCfg.set(key, defCfg.get(key));
                    changed = true;
                }
            }
            if (changed) {
                serverCfg.save(file);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aAuto-updated craft.yml with missing keys."));
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to merge craft.yml defaults: " + e.getMessage());
        }
    }

    private Component parseComponent(String text) {
        String converted = text
                .replace("&0", "<black>").replace("&1", "<dark_blue>")
                .replace("&2", "<dark_green>").replace("&3", "<dark_aqua>")
                .replace("&4", "<dark_red>").replace("&5", "<dark_purple>")
                .replace("&6", "<gold>").replace("&7", "<gray>")
                .replace("&8", "<dark_gray>").replace("&9", "<blue>")
                .replace("&a", "<green>").replace("&b", "<aqua>")
                .replace("&c", "<red>").replace("&d", "<light_purple>")
                .replace("&e", "<yellow>").replace("&f", "<white>")
                .replace("&k", "<obfuscated>").replace("&l", "<bold>")
                .replace("&m", "<strikethrough>").replace("&n", "<underline>")
                .replace("&o", "<italic>").replace("&r", "<reset>");
        return miniMessage.deserialize(converted);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisableCommand() {
        return enabled && disableCommand;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isRequirePermission() {
        return requirePermission;
    }

    public String getPermission() {
        return permission;
    }

    public ItemStack getCraftItem() {
        return craftItem != null ? craftItem.clone() : null;
    }

    public NamespacedKey getPdcKey() {
        return pdcKey;
    }

    public boolean isCraftItem(ItemStack item) {
        if (!enabled || item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        return meta.getPersistentDataContainer().has(pdcKey, PersistentDataType.BYTE);
    }

    public void shutdown() {
        unregisterRecipe();
    }
}
