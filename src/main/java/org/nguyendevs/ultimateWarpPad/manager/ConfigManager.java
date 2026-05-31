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
    private boolean allowDamageCancel;
    private boolean forceStay;
    private int cooldown;
    private int maxWarpsPerPlayer;
    private boolean groupTeleporting;
    private boolean center;
    private List<String> waypointIcons;
    private List<String> disabledWorlds;
    private boolean particleEnabled;
    private Particle particleType;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.saveDefaultConfig();
        autoUpdateKeys();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        launchY = config.getInt("travel.launch_y", 500);
        applyDarkness = config.getBoolean("travel.apply_darkness", true);
        allowDamageCancel = config.getBoolean("travel.allow_damage_cancel", true);
        forceStay = config.getBoolean("travel.force_stay", true);
        cooldown = config.getInt("travel.cooldown", -1);
        groupTeleporting = config.getBoolean("travel.group-teleporting", true);
        center = config.getBoolean("travel.center", true);
        maxWarpsPerPlayer = config.getInt("max_warps_per_player", 5);
        waypointIcons = config.getStringList("warp-icons");
        if (waypointIcons.isEmpty()) {
            waypointIcons = List.of("NETHER_STAR", "DIAMOND", "EMERALD", "GOLD_INGOT");
        }
        disabledWorlds = config.getStringList("disabled-worlds");
        particleEnabled = config.getBoolean("particle.enabled", true);
        try {
            particleType = Particle.valueOf(config.getString("particle.type", "FIREWORK"));
        } catch (IllegalArgumentException e) {
            particleType = Particle.FIREWORK;
        }
    }

    public int getLaunchY() {
        return launchY;
    }

    public boolean isApplyDarkness() {
        return applyDarkness;
    }

    public boolean isAllowDamageCancel() {
        return allowDamageCancel;
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

    private void autoUpdateKeys() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        try (InputStream defStream = plugin.getResource("config.yml")) {
            if (defStream == null) return;
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
