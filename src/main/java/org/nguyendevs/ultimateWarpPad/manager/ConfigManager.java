package org.nguyendevs.ultimateWarpPad.manager;

import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ConfigManager {

    private final JavaPlugin plugin;
    private int launchY;
    private boolean applyDarkness;
    private boolean applyVanish;
    private boolean applyGlowing;
    private boolean applyRegeneration;
    private boolean forceStay;
    private int cooldown;
    private int maxWarpsPerPlayer;
    private boolean groupTeleporting;
    private boolean groupCollision;
    private int groupMaxPerWarp;
    private int groupDelayInTick;
    private boolean center;
    private List<String> waypointIcons;
    private List<String> disabledWorlds;
    private boolean particleEnabled;
    private Particle particleType;
    private int idleParticleAmount;
    private int triggerParticleAmount;
    private boolean messageChatEnabled;
    private boolean messageActionBarEnabled;
    private boolean messageBossBarEnabled;
    private boolean messageTitleEnabled;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        autoUpdateKeys();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        launchY = config.getInt("warp.launch-y", 500);
        applyDarkness = config.getBoolean("effect.apply-darkness", true);
        applyVanish = config.getBoolean("effect.apply-vanish", true);
        applyGlowing = config.getBoolean("effect.apply-glowing", true);
        applyRegeneration = config.getBoolean("effect.apply_regeneration", false);
        forceStay = config.getBoolean("warp.force-stay", true);
        cooldown = config.getInt("warp.cooldown", -1);
        groupTeleporting = config.getBoolean("group-teleport.enable", true);
        groupCollision = config.getBoolean("group-teleport.collision", true);
        groupMaxPerWarp = config.getInt("group-teleport.max-per-warp", -1);
        groupDelayInTick = config.getInt("group-teleport.delay-in-tick", 5);
        center = config.getBoolean("warp.center", true);
        maxWarpsPerPlayer = config.getInt("max-warps-per-player", 5);
        waypointIcons = loadIconsyml();
        disabledWorlds = config.getStringList("disabled-worlds");
        particleEnabled = config.getBoolean("particle.enabled", true);
        try {
            particleType = Particle.valueOf(config.getString("particle.type", "FIREWORK"));
        } catch (IllegalArgumentException e) {
            particleType = Particle.FIREWORK;
        }
        idleParticleAmount = config.getInt("particle.idle-amount", 3);
        triggerParticleAmount = config.getInt("particle.trigger-amount", 4);
        messageChatEnabled = config.getBoolean("message.chat", true);
        messageActionBarEnabled = config.getBoolean("message.action-bar", false);
        messageBossBarEnabled = config.getBoolean("message.boss-bar", false);
        messageTitleEnabled = config.getBoolean("message.title", false);
    }

    public int getLaunchY() {
        return launchY;
    }

    public boolean isApplyDarkness() {
        return applyDarkness;
    }

    public boolean isApplyVanish() {
        return applyVanish;
    }

    public boolean isApplyGlowing() {
        return applyGlowing;
    }

    public boolean isApplyRegeneration() {
        return applyRegeneration;
    }

    public boolean isForceStay() {
        return forceStay;
    }

    public int getCooldown() {
        return cooldown;
    }

    public int getMaxWarpsPerPlayer() {
        return maxWarpsPerPlayer;
    }

    public boolean isGroupTeleporting() {
        return groupTeleporting;
    }

    public boolean isGroupCollision() {
        return groupCollision;
    }

    public int getGroupMaxPerWarp() {
        return groupMaxPerWarp;
    }

    public int getGroupDelayInTick() {
        return groupDelayInTick;
    }

    public boolean isCenter() {
        return center;
    }

    public List<String> getWaypointIcons() {
        return waypointIcons;
    }

    public boolean isDisabledWorld(String worldName) {
        return disabledWorlds.contains(worldName);
    }

    public List<String> getDisabledWorlds() {
        return disabledWorlds;
    }

    public boolean isParticleEnabled() {
        return particleEnabled;
    }

    public Particle getParticleType() {
        return particleType;
    }

    public int getIdleParticleAmount() {
        return idleParticleAmount;
    }

    public int getTriggerParticleAmount() {
        return triggerParticleAmount;
    }

    public boolean isMessageChatEnabled() {
        return messageChatEnabled;
    }

    public boolean isMessageActionBarEnabled() {
        return messageActionBarEnabled;
    }

    public boolean isMessageBossBarEnabled() {
        return messageBossBarEnabled;
    }

    public boolean isMessageTitleEnabled() {
        return messageTitleEnabled;
    }

    private List<String> loadIconsyml() {
        File iconsFile = new File(plugin.getDataFolder(), "icons.yml");
        if (!iconsFile.exists()) {
            plugin.saveResource("icons.yml", false);
        }
        YamlConfiguration iconsConfig = YamlConfiguration.loadConfiguration(iconsFile);
        try (java.io.InputStream defStream = plugin.getResource("icons.yml")) {
            if (defStream != null) {
                YamlConfiguration defIcons = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defStream, StandardCharsets.UTF_8));
                boolean changed = false;
                for (String key : defIcons.getKeys(true)) {
                    if (!defIcons.isConfigurationSection(key) && !iconsConfig.contains(key)) {
                        iconsConfig.set(key, defIcons.get(key));
                        changed = true;
                    }
                }
                if (changed) {
                    iconsConfig.save(iconsFile);
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to merge icons.yml defaults: " + e.getMessage());
        }
        List<String> icons = iconsConfig.getStringList("icons");
        if (icons.isEmpty()) {
            icons = List.of("NETHER_STAR", "DIAMOND", "EMERALD", "GOLD_INGOT");
        }
        return icons;
    }

    private void autoUpdateKeys() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        try (InputStream defStream = plugin.getResource("config.yml")) {
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
                plugin.getLogger().info("Auto-updated config.yml with missing keys.");
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to auto-update config.yml: " + e.getMessage());
        }
    }
}
