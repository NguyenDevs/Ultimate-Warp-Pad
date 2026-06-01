package org.nguyendevs.ultimateWarpPad.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.nguyendevs.ultimateWarpPad.gui.SettingsGUI;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.CraftManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;
import org.nguyendevs.ultimateWarpPad.model.CostType;

import java.util.*;
import java.util.stream.Collectors;

public class AdminWarpCommand implements CommandExecutor, TabCompleter {

    private final WarpManager warpManager;
    private final ConfigManager configManager;
    private final MessageManager messageManager;
    private final SettingsGUI settingsGUI;
    private final CraftManager craftManager;

    public AdminWarpCommand(WarpManager warpManager, ConfigManager configManager, MessageManager messageManager,
            SettingsGUI settingsGUI, CraftManager craftManager) {
        this.warpManager = warpManager;
        this.configManager = configManager;
        this.messageManager = messageManager;
        this.settingsGUI = settingsGUI;
        this.craftManager = craftManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("uwp.admin")) {
            messageManager.send(sender, "error.permission");
            playErrorSound(sender);
            return true;
        }

        if (args.length == 0) {
            messageManager.send(sender, "error.invalid_syntax",
                    Map.of("usage", "/wpa <reload|create|delete|setting>"));
            playErrorSound(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(sender);
            case "create" -> handleCreate(sender, args);
            case "delete" -> handleDelete(sender, args);
            case "setting" -> handleSetting(sender, args);
            default -> {
                messageManager.send(sender, "error.invalid_syntax",
                        Map.of("usage", "/wpa <reload|create|delete|setting>"));
                playErrorSound(sender);
            }
        }
        return true;
    }

    private void handleReload(CommandSender sender) {
        configManager.load();
        messageManager.load();
        craftManager.load();
        messageManager.send(sender, "config.reloaded");
        playSuccessSound(sender);
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messageManager.send(sender, "error.player_only");
            return;
        }

        if (configManager.isDisabledWorld(player.getWorld().getName())) {
            messageManager.send(sender, "error.disabled_world");
            playErrorSound(sender);
            return;
        }

        if (args.length < 2) {
            messageManager.send(sender, "error.invalid_syntax",
                    Map.of("usage", "/wpa create <warp_id> [warp_name]"));
            playErrorSound(sender);
            return;
        }

        String warpId = args[1];
        if (!warpId.matches("^[a-zA-Z0-9_-]+$")) {
            messageManager.send(sender, "warp.invalid_id");
            playErrorSound(sender);
            return;
        }

        if (warpManager.isWarpIdTaken(null, warpId)) {
            messageManager.send(sender, "warp.already_exists", Map.of("id", warpId));
            playErrorSound(sender);
            return;
        }

        String warpName = args.length >= 3 ? args[2] : getDefaultWarpName(null);

        Warp warp = new Warp();
        warp.setOwner(null);
        warp.setWarpId(warpId);
        warp.setWarpName(warpName);
        warp.setLocation(player.getLocation());
        warp.setType(WarpType.ADMIN);
        warp.setCostType(CostType.FREE);
        warp.setCost(-1);
        warp.setRange(-1);

        if (!warpManager.isSkyClear(player.getLocation())) {
            messageManager.send(sender, "error.blocked_above");
            playErrorSound(sender);
            return;
        }

        if (warpManager.getOverlappingWarpForAdmin(player.getLocation()) != null) {
            messageManager.send(sender, "warp.overlaps_existing");
            playErrorSound(sender);
            return;
        }

        org.bukkit.Location warpLoc = player.getLocation().clone();
        org.bukkit.Location tpLoc = warpLoc.clone();
        tpLoc.setY(tpLoc.getY() + 2);
        player.teleport(tpLoc);
        warp.setLocation(warpLoc);

        if (warpManager.createWarp(warp)) {
            messageManager.send(sender, "warp.created", Map.of("name", warpName));
            playCreateSound(sender);
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messageManager.send(sender, "error.invalid_syntax",
                    Map.of("usage", "/wpa delete <warp_id>"));
            playErrorSound(sender);
            return;
        }

        Warp warp = warpManager.getWarp(null, args[1]);
        if (warp == null || !warp.isAdminWarp()) {
            messageManager.send(sender, "warp.not_found", Map.of("id", args[1]));
            playErrorSound(sender);
            return;
        }

        warpManager.deleteWarp(warp);
        messageManager.send(sender, "warp.deleted", Map.of("id", args[1]));
        playDeleteSound(sender);
    }

    private void handleSetting(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            messageManager.send(sender, "error.player_only");
            return;
        }

        Warp warp = null;
        if (args.length >= 2) {
            warp = warpManager.getWarp(null, args[1]);
        } else {
            warp = warpManager.getWarpAtLocation(player.getLocation());
        }

        if (warp == null) {
            messageManager.send(sender, "warp.not_found_standing");
            playErrorSound(sender);
            return;
        }

        if (!warp.isAdminWarp()) {
            messageManager.send(sender, "error.permission");
            playErrorSound(sender);
            return;
        }

        playSuccessSound(sender);
        settingsGUI.open(player, warp);
    }

    private void playErrorSound(CommandSender sender) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
        }
    }

    private void playSuccessSound(CommandSender sender) {
        if (sender instanceof Player player) {
            player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.5f);
        }
    }

    private void playCreateSound(CommandSender sender){
        if(sender instanceof Player player){
            player.playSound(player.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1.0f, 0.8f);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_HIT, 1.0f, 0.5f);
            player.playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, 1.0f, 0.5f);
            
        }
    }

    private void playDeleteSound(CommandSender sender){
        if(sender instanceof Player player){
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0f, 0.8f);
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_CLUSTER_BREAK, 1.0f, 0.3f);
            player.playSound(player.getLocation(), Sound.BLOCK_SCULK_SHRIEKER_BREAK, 1.0f, 0.5f);
        }
    }

    private String getDefaultWarpName(UUID owner) {
        int count;
        if (owner == null) {
            count = warpManager.getAdminWarps().size();
        } else {
            count = warpManager.getPlayerWarpCount(owner);
        }
        return "Warp " + WarpManager.toRoman(count + 1);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("uwp.admin"))
            return Collections.emptyList();

        if (args.length == 1) {
            return List.of("reload", "create", "delete", "setting").stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "delete" -> {
                    List<String> ids = warpManager.getAdminWarpIds();
                    if (!args[1].isEmpty()) {
                        ids = ids.stream().filter(id -> id.startsWith(args[1])).collect(Collectors.toList());
                    }
                    return ids;
                }
                case "setting" -> {
                    List<String> ids = warpManager.getAdminWarpIds();
                    if (!args[1].isEmpty()) {
                        ids = ids.stream().filter(id -> id.startsWith(args[1])).collect(Collectors.toList());
                    }
                    return ids;
                }
            }
        }

        return Collections.emptyList();
    }
}
