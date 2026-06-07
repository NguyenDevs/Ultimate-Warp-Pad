package org.nguyendevs.ultimateWarpPad.command;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import net.kyori.adventure.text.Component;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
            case "give" -> handleGive(sender, args);
            case "info" -> handleInfo(sender);
            case "fixall" -> handleFixAll(sender);
            default -> {
                messageManager.send(sender, "error.invalid_syntax",
                        Map.of("usage", "/wpa <reload|create|delete|setting|give|info|fixall>"));
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

    private void handleGive(CommandSender sender, String[] args) {
        if (args.length < 2) {
            messageManager.send(sender, "error.invalid_syntax",
                    Map.of("usage", "/wpa give <player> [amount]"));
            playErrorSound(sender);
            return;
        }

        org.bukkit.entity.Player target = org.bukkit.Bukkit.getPlayer(args[1]);
        if (target == null) {
            messageManager.send(sender, "error.player_not_found", Map.of("player", args[1]));
            playErrorSound(sender);
            return;
        }

        int amount = 1;
        if (args.length >= 3) {
            try {
                amount = Integer.parseInt(args[2]);
                if (amount < 1 || amount > 64) {
                    messageManager.send(sender, "craft.give_invalid_amount");
                    playErrorSound(sender);
                    return;
                }
            } catch (NumberFormatException e) {
                messageManager.send(sender, "craft.give_invalid_amount");
                playErrorSound(sender);
                return;
            }
        }

        org.bukkit.inventory.ItemStack item = craftManager.getCraftItem();
        if (item == null) {
            messageManager.send(sender, "craft.not_configured");
            playErrorSound(sender);
            return;
        }

        item.setAmount(amount);
        target.getInventory().addItem(item);
        messageManager.send(sender, "craft.give_success",
                Map.of("player", target.getName(), "amount", String.valueOf(amount)));
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
        warp.setIcon(configManager.getDefaultAdminWarpIcon());
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

    private void handleInfo(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            messageManager.send(sender, "error.player_only");
            return;
        }

        Warp warp = warpManager.getWarpAtLocation(player.getLocation());
        if (warp == null) {
            messageManager.send(sender, "warp.not_found_standing");
            playErrorSound(sender);
            return;
        }

        String typeStr = warp.isAdminWarp() ? "WPA" : "WPP";
        int x = warp.getLocation().getBlockX();
        int y = warp.getLocation().getBlockY();
        int z = warp.getLocation().getBlockZ();
        String worldName = warp.getWorld().getName();

        String ownerName;
        if (warp.getOwner() == null) {
            ownerName = "Admin";
        } else {
            org.bukkit.OfflinePlayer op = org.bukkit.Bukkit.getOfflinePlayer(warp.getOwner());
            ownerName = op.getName() != null ? op.getName() : warp.getOwner().toString();
        }

        String costStr;
        if (warp.getCost() < 0) {
            costStr = "Free";
        } else {
            costStr = (int) warp.getCost() + " " + warp.getCostType().name();
        }

        String regionName = "uwp_" + warp.getCompositeId();

        Map<String, String> placeholders = Map.of(
                "id", warp.getWarpId(),
                "owner", ownerName,
                "type", typeStr,
                "x", String.valueOf(x),
                "y", String.valueOf(y),
                "z", String.valueOf(z),
                "world", worldName,
                "cost", costStr,
                "region", regionName
        );

        for (Component line : messageManager.getComponentList("warp.info", placeholders)) {
            sender.sendMessage(line);
        }
        playSuccessSound(sender);
    }

    private void handleFixAll(CommandSender sender) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        int removed = 0;

        for (World world : Bukkit.getServer().getWorlds()) {
            RegionManager rm = container.get(BukkitAdapter.adapt(world));
            if (rm == null) continue;

            Set<String> toRemove = new HashSet<>();
            for (String regionId : rm.getRegions().keySet()) {
                if (!regionId.startsWith("uwp_")) continue;

                String compositeId = regionId.substring(4);
                if (warpManager.getWarp(compositeId) == null) {
                    toRemove.add(regionId);
                }
            }

            for (String regionId : toRemove) {
                rm.removeRegion(regionId);
                removed++;
            }
        }

        messageManager.send(sender, "admin.ghost_removed", Map.of("count", String.valueOf(removed)));
        if (removed > 0) {
            playSuccessSound(sender);
        }
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
            return List.of("reload", "create", "delete", "setting", "give", "info", "fixall").stream()
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
                case "give" -> {
                    return org.bukkit.Bukkit.getOnlinePlayers().stream()
                            .map(org.bukkit.entity.Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            return List.of("1", "4", "8", "16", "32", "64").stream()
                    .filter(n -> n.startsWith(args[2]))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
