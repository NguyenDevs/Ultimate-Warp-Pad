package org.nguyendevs.ultimateWarpPad;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.nguyendevs.ultimateWarpPad.command.AdminWarpCommand;
import org.nguyendevs.ultimateWarpPad.command.PlayerWarpCommand;
import org.nguyendevs.ultimateWarpPad.database.DatabaseManager;
import org.nguyendevs.ultimateWarpPad.gui.IconSelectionGUI;
import org.nguyendevs.ultimateWarpPad.gui.SettingsGUI;
import org.nguyendevs.ultimateWarpPad.gui.WarpSelectionGUI;
import org.nguyendevs.ultimateWarpPad.listener.WarpListener;
import org.nguyendevs.ultimateWarpPad.manager.*;
import org.nguyendevs.ultimateWarpPad.travel.TravelQueue;
import org.nguyendevs.ultimateWarpPad.schematic.WarpSchematicData;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UltimateWarpPad extends JavaPlugin {

    public static final Set<UUID> FALL_DAMAGE_IMMUNE = ConcurrentHashMap.newKeySet();

    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private AnimationManager animationManager;
    private WarpManager warpManager;
    private SettingsGUI settingsGUI;
    private WarpSelectionGUI warpSelectionGUI;
    private IconSelectionGUI iconSelectionGUI;
    private WarpListener warpListener;
    private WarpParticleManager warpParticleManager;
    private TravelQueue travelQueue;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("Ultimate Warp Pad is loading...");

        configManager = new ConfigManager(this);
        configManager.load();

        messageManager = new MessageManager(this);
        messageManager.load();
        messageManager.setConfigManager(configManager);

        databaseManager = new DatabaseManager(this);
        databaseManager.buildWorldCache();
        databaseManager.init().thenRun(() -> {
            getLogger().info("Database initialized");
        });

        animationManager = new AnimationManager(this);

        warpManager = new WarpManager(this, databaseManager, configManager, animationManager);
        warpManager.loadAllWarps().thenRun(() -> {
            getLogger().info("All warps loaded");
        });

        settingsGUI = new SettingsGUI(warpManager, messageManager);
        iconSelectionGUI = new IconSelectionGUI(warpManager, messageManager, configManager, settingsGUI);
        settingsGUI.setIconSelectionGUI(iconSelectionGUI);

        travelQueue = new TravelQueue(entry -> Bukkit.getScheduler().runTask(this, () ->
                warpSelectionGUI.startTravel(entry.player(), entry.source(), entry.destination())));

        warpSelectionGUI = new WarpSelectionGUI(this, warpManager, messageManager, configManager, animationManager, travelQueue);

        AdminWarpCommand adminCmd = new AdminWarpCommand(warpManager, configManager, messageManager, settingsGUI);
        PlayerWarpCommand playerCmd = new PlayerWarpCommand(warpManager, messageManager, settingsGUI, configManager);

        getCommand("wpa").setExecutor(adminCmd);
        getCommand("wpa").setTabCompleter(adminCmd);
        getCommand("wpp").setExecutor(playerCmd);
        getCommand("wpp").setTabCompleter(playerCmd);

        warpListener = new WarpListener(this, warpManager, messageManager, configManager,
                warpSelectionGUI, settingsGUI, iconSelectionGUI);
        Bukkit.getPluginManager().registerEvents(warpListener, this);

        warpParticleManager = new WarpParticleManager(this, warpManager, configManager, animationManager);
        warpParticleManager.start();

        printLogo();
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aWorldGuard hooked successfully!"));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &cWorldGuard not found! Disabling plugin."));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        if (Bukkit.getPluginManager().getPlugin("Vault") != null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aVault hooked successfully!"));
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &cVault not found! Economy features disabled."));
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&3[&bUltimateWarpPad&3] &aUltimateWarpPad plugin enabled successfully!"));
    }

    @Override
    public void onDisable() {
        if (warpListener != null) {
            warpListener.cleanup();
        }
        if (animationManager != null) {
            animationManager.cancelAll();
        }
        if (warpManager != null) {
            warpManager.removeAllRegions();
        }
        if (warpParticleManager != null) {
            warpParticleManager.stop();
        }
        if (databaseManager != null) {
            databaseManager.close();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&3[&bUltimateWarpPad&3] &cUltimateWarpPad plugin disabled!"));
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public void printLogo() {
        var s = Bukkit.getConsoleSender();
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f   ██╗   ██╗██╗  ████████╗██╗███╗   ███╗ █████╗ ████████╗███████╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f   ██║   ██║██║  ╚══██╔══╝██║████╗ ████║██╔══██╗╚══██╔══╝██╔════╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f   ██║   ██║██║     ██║   ██║██╔████╔██║███████║   ██║   █████╗  "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&f   ██║   ██║██║     ██║   ██║██║╚██╔╝██║██╔══██║   ██║   ██╔══╝  "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b   ╚██████╔╝███████╗██║   ██║██║ ╚═╝ ██║██║  ██║   ██║   ███████╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b    ╚═════╝ ╚══════╝╚═╝   ╚═╝╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚══════╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b   ██╗    ██╗ █████╗ ██████╗ ██████╗     ██████╗  █████╗ ██████╗ "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b   ██║    ██║██╔══██╗██╔══██╗██╔══██╗    ██╔══██╗██╔══██╗██╔══██╗"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3   ██║ █╗ ██║███████║██████╔╝██████╔╝    ██████╔╝███████║██║  ██║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3   ██║███╗██║██╔══██║██╔══██╗██╔═══╝     ██╔═══╝ ██╔══██║██║  ██║"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3   ╚███╔███╔╝██║  ██║██║  ██║██║         ██║     ██║  ██║██████╔╝"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3    ╚══╝╚══╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝         ╚═╝     ╚═╝  ╚═╝╚═════╝ "));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4         &fUltimate &bWarp &3Pad"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&b         Version " + getDescription().getVersion()));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',"&3         Development by NguyenDevs"));
        s.sendMessage(ChatColor.translateAlternateColorCodes('&',""));
    }
}
