package org.nguyendevs.ultimateWarpPad.manager;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageManager {

    private final JavaPlugin plugin;
    private final MiniMessage miniMessage;
    private ConfigManager configManager;
    private YamlConfiguration messages;
    private String prefix;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    public void load() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        autoUpdateKeys();

        messages = YamlConfiguration.loadConfiguration(file);

        try (InputStream defStream = plugin.getResource("messages.yml")) {
            if (defStream != null) {
                YamlConfiguration defMessages = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defStream, StandardCharsets.UTF_8));
                messages.setDefaults(defMessages);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load default messages");
        }

        prefix = messages.getString("prefix", "<gradient:#FF6B6B:#FFE66D>UltimateWarpPad</gradient>");
    }

    private String convertLegacyCodes(String text) {
        if (!text.contains("&"))
            return text;
        return text
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
    }

    public String getRaw(String path) {
        return messages.getString(path, "<red>Message not found: " + path + "</red>");
    }

    public List<String> getRawList(String path) {
        return messages.getStringList(path);
    }

    public Component get(String path) {
        return miniMessage.deserialize(convertLegacyCodes(getRaw(path)));
    }

    public Component get(String path, Map<String, String> placeholders) {
        String msg = getRaw(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return miniMessage.deserialize(convertLegacyCodes(msg));
    }

    public Component getPrefixed(String path) {
        String msg = getRaw("prefix") + " " + getRaw(path);
        return miniMessage.deserialize(convertLegacyCodes(msg));
    }

    public Component getPrefixed(String path, Map<String, String> placeholders) {
        String msg = getRaw("prefix") + " " + getRaw(path);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return miniMessage.deserialize(convertLegacyCodes(msg));
    }

    public void send(CommandSender sender, String path) {
        sender.sendMessage(getPrefixed(path));
    }

    public void send(Player player, String path) {
        player.sendMessage(getPrefixed(path));
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        sender.sendMessage(getPrefixed(path, placeholders));
    }

    public void send(Player player, String path, Map<String, String> placeholders) {
        player.sendMessage(getPrefixed(path, placeholders));
    }

    public void sendRaw(CommandSender sender, String path) {
        sender.sendMessage(get(path));
    }

    public void sendRaw(Player player, String path) {
        player.sendMessage(get(path));
    }

    public void sendRaw(CommandSender sender, String path, Map<String, String> placeholders) {
        sender.sendMessage(get(path, placeholders));
    }

    public void sendRaw(Player player, String path, Map<String, String> placeholders) {
        player.sendMessage(get(path, placeholders));
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public void sendTravel(Player player, String key, Map<String, String> placeholders) {
        if (configManager == null) {
            send(player, "travel_chat." + key, placeholders);
            return;
        }

        if (configManager.isMessageChatEnabled()) {
            String raw = getRaw("travel_chat." + key);
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                raw = raw.replace("%" + e.getKey() + "%", e.getValue());
            }
            player.sendMessage(miniMessage.deserialize(convertLegacyCodes(getRaw("prefix") + " " + raw)));
        }

        if (configManager.isMessageActionBarEnabled()) {
            String raw = getRaw("travel_action_bar." + key);
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                raw = raw.replace("%" + e.getKey() + "%", e.getValue());
            }
            player.sendActionBar(miniMessage.deserialize(convertLegacyCodes(raw)));
        }

        if (configManager.isMessageTitleEnabled()) {
            String titleRaw = getRaw("travel_title." + key);
            String subRaw = getRaw("travel_title." + key + "_st");
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                titleRaw = titleRaw.replace("%" + e.getKey() + "%", e.getValue());
                subRaw = subRaw.replace("%" + e.getKey() + "%", e.getValue());
            }
            Component titleComp = miniMessage.deserialize(convertLegacyCodes(titleRaw));
            Component subComp = subRaw.isEmpty() ? Component.empty()
                    : miniMessage.deserialize(convertLegacyCodes(subRaw));
            Title.Times times = Title.Times.times(
                    Duration.ofMillis(200), Duration.ofMillis(2000), Duration.ofMillis(500));
            player.showTitle(Title.title(titleComp, subComp, times));
        }
    }

    public void sendTravel(Player player, String key) {
        sendTravel(player, key, Map.of());
    }

    public Component formatColored(String text) {
        if (text.contains("&")) {
            text = LegacyComponentSerializer.legacyAmpersand().serialize(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(text));
        }
        return miniMessage.deserialize(text);
    }

    public String translateColorCodes(String text) {
        return text.replace('&', '§');
    }

    public Component buildComponent(String text) {
        String processed = translateColorCodes(text);
        return miniMessage.deserialize(processed);
    }

    public List<Component> getComponentList(String path) {
        List<String> raw = messages.getStringList(path);
        List<Component> result = new java.util.ArrayList<>();
        for (String line : raw) {
            result.add(miniMessage.deserialize(convertLegacyCodes(line)));
        }
        return result;
    }

    public List<Component> getComponentList(String path, Map<String, String> placeholders) {
        List<String> raw = messages.getStringList(path);
        List<Component> result = new java.util.ArrayList<>();
        for (String line : raw) {
            String msg = line;
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                msg = msg.replace("%" + entry.getKey() + "%", entry.getValue());
            }
            result.add(miniMessage.deserialize(convertLegacyCodes(msg)));
        }
        return result;
    }

    void autoUpdateKeys() {
        File file = new File(plugin.getDataFolder(), "messages.yml");
        try (InputStream defStream = plugin.getResource("messages.yml")) {
            if (defStream == null)
                return;
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(defStream, StandardCharsets.UTF_8));
            YamlConfiguration serverConfig = YamlConfiguration.loadConfiguration(file);

            boolean changed = false;
            for (String key : defConfig.getKeys(true)) {
                if (!defConfig.isConfigurationSection(key) && !serverConfig.contains(key)) {
                    serverConfig.set(key, defConfig.get(key));
                    changed = true;
                }
            }
            if (changed) {
                serverConfig.save(file);
                plugin.getLogger().info("Auto-updated messages.yml with missing keys.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to auto-update messages.yml: " + e.getMessage());
        }
    }
}
