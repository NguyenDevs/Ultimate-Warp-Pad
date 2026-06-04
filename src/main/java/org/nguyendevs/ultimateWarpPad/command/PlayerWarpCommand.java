package org.nguyendevs.ultimateWarpPad.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.nguyendevs.ultimateWarpPad.gui.SettingsGUI;
import org.nguyendevs.ultimateWarpPad.flag.UWPFlags;
import org.nguyendevs.ultimateWarpPad.manager.CraftManager;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.manager.WarpManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.model.WarpType;
import org.nguyendevs.ultimateWarpPad.model.CostType;

import java.util.*;
import java.util.stream.Collectors;

public class PlayerWarpCommand implements CommandExecutor, TabCompleter {

    private final WarpManager warpManager;
    private final MessageManager messageManager;
    private final SettingsGUI settingsGUI;
    private final ConfigManager configManager;
    private final CraftManager craftManager;

    public PlayerWarpCommand(WarpManager warpManager, MessageManager messageManager,
                             SettingsGUI settingsGUI, ConfigManager configManager, CraftManager craftManager) {
        this.warpManager = warpManager;
        this.messageManager = messageManager;
        this.settingsGUI = settingsGUI;
        this.configManager = configManager;
        this.craftManager = craftManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command is for players only!");
            return true;
        }

        if (args.length == 0) {
            messageManager.send(player, "error.invalid_syntax",
                    Map.of("usage", "/wpp <create|delete|setting|trust|help>"));
            playErrorSound(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> handleCreate(player, args);
            case "delete" -> handleDelete(player, args);
            case "setting" -> handleSetting(player, args);
            case "trust" -> handleTrust(player, args);
            case "help" -> handleHelp(player);
            default -> {
                messageManager.send(player, "error.invalid_syntax",
                        Map.of("usage", "/wpp <create|delete|setting|trust|help>"));
                playErrorSound(player);
            }
        }
        return true;
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("uwp.user.create")) {
            messageManager.send(player, "error.permission");
            playErrorSound(player);
            return;
        }

        if (craftManager.isEnabled() && craftManager.isDisableCommand()) {
            org.bukkit.inventory.ItemStack hand = player.getInventory().getItemInMainHand();
            if (!craftManager.isCraftItem(hand)) {
                messageManager.send(player, "craft.item_required");
                playErrorSound(player);
                return;
            }
        }

        if (configManager.isDisabledWorld(player.getWorld().getName())) {
            messageManager.send(player, "error.disabled_world");
            playErrorSound(player);
            return;
        }

        if (args.length < 2) {
            messageManager.send(player, "error.invalid_syntax",
                    Map.of("usage", "/wpp create <warp_id> [warp_name]"));
            playErrorSound(player);
            return;
        }

        String warpId = args[1];
        if (!warpId.matches("^[a-zA-Z0-9_-]+$")) {
            messageManager.send(player, "warp.invalid_id");
            playErrorSound(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        if (warpManager.isWarpIdTaken(uuid, warpId)) {
            messageManager.send(player, "warp.already_exists", Map.of("id", warpId));
            playErrorSound(player);
            return;
        }

        int max = warpManager.getEffectiveMaxWarps(player);
        if (warpManager.getPlayerWarpCount(uuid) >= max) {
            messageManager.send(player, "warp.max_reached", Map.of("max", String.valueOf(max)));
            playErrorSound(player);
            return;
        }

        String warpName = args.length >= 3 ? args[2] : getDefaultWarpName(uuid);

        Warp warp = new Warp();
        warp.setOwner(uuid);
        warp.setWarpId(warpId);
        warp.setWarpName(warpName);
        warp.setLocation(player.getLocation());
        warp.setType(WarpType.PLAYER);
        warp.setIcon(configManager.getDefaultPlayerWarpIcon());
        warp.setCostType(CostType.XP);
        warp.setCost(-1);
        warp.setRange(1000);

        if (!UWPFlags.checkFlag(player.getLocation(), UWPFlags.UWP_PLACE)) {
            messageManager.send(player, "error.place_denied");
            playErrorSound(player);
            return;
        }

        if (!warpManager.isSkyClear(player.getLocation())) {
            messageManager.send(player, "error.blocked_above");
            playErrorSound(player);
            return;
        }

        if (warpManager.getOverlappingWarp(player.getLocation()) != null) {
            messageManager.send(player, "warp.overlaps_existing");
            playErrorSound(player);
            return;
        }

        org.bukkit.Location warpLoc = player.getLocation().clone();
        org.bukkit.Location tpLoc = warpLoc.clone();
        tpLoc.setY(tpLoc.getY() + 1);
        player.teleport(tpLoc);
        warp.setLocation(warpLoc);

        if (warpManager.createWarp(warp)) {
            if (craftManager.isEnabled() && craftManager.isDisableCommand()) {
                org.bukkit.inventory.ItemStack hand = player.getInventory().getItemInMainHand();
                hand.setAmount(hand.getAmount() - 1);
            }
            messageManager.send(player, "warp.created", Map.of("name", warpName));
            playCreateSound(player);
        }
    }

    private void handleDelete(Player player, String[] args) {
        if (!player.hasPermission("uwp.user.delete")) {
            messageManager.send(player, "error.permission");
            playErrorSound(player);
            return;
        }

        if (args.length < 2) {
            messageManager.send(player, "error.invalid_syntax",
                    Map.of("usage", "/wpp delete <warp_id>"));
            playErrorSound(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        Warp warp = warpManager.getWarp(uuid, args[1]);
        if (warp == null) {
            messageManager.send(player, "warp.not_found", Map.of("id", args[1]));
            playErrorSound(player);
            return;
        }

        if (!warp.isOwner(uuid)) {
            messageManager.send(player, "warp.not_owner");
            playErrorSound(player);
            return;
        }

        warpManager.deleteWarp(warp);
        messageManager.send(player, "warp.deleted", Map.of("id", args[1]));
        playDeleteSound(player);
    }

    private void handleSetting(Player player, String[] args) {
        if (!player.hasPermission("uwp.user.setting")) {
            messageManager.send(player, "error.permission");
            playErrorSound(player);
            return;
        }

        Warp warp = null;
        if (args.length >= 2) {
            warp = warpManager.getWarp(player.getUniqueId(), args[1]);
        } else {
            warp = warpManager.getWarpAtLocation(player.getLocation());
        }

        if (warp == null) {
            messageManager.send(player, "warp.not_found_standing");
            playErrorSound(player);
            return;
        }

        if (!warp.isOwner(player.getUniqueId())) {
            messageManager.send(player, "warp.not_owner");
            playErrorSound(player);
            return;
        }

        playSuccessSound(player);
        settingsGUI.open(player, warp);
    }

    private void handleTrust(Player player, String[] args) {
        if (!player.hasPermission("uwp.user.trust")) {
            messageManager.send(player, "error.permission");
            playErrorSound(player);
            return;
        }

        if (args.length < 3) {
            messageManager.send(player, "error.invalid_syntax",
                    Map.of("usage", "/wpp trust <warp_id> <player>"));
            playErrorSound(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        Warp warp = warpManager.getWarp(uuid, args[1]);
        if (warp == null) {
            messageManager.send(player, "warp.not_found", Map.of("id", args[1]));
            playErrorSound(player);
            return;
        }

        if (!warp.isOwner(uuid)) {
            messageManager.send(player, "warp.not_owner");
            playErrorSound(player);
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            messageManager.send(player, "error.player_not_found", Map.of("player", args[2]));
            playErrorSound(player);
            return;
        }

        UUID targetUUID = target.getUniqueId();
        if (warp.getTrustedPlayers().contains(targetUUID)) {
            warp.getTrustedPlayers().remove(targetUUID);
            messageManager.send(player, "warp.trust_removed", Map.of("player", target.getName()));
        } else {
            warp.getTrustedPlayers().add(targetUUID);
            messageManager.send(player, "warp.trust_added", Map.of("player", target.getName()));
        }

        warpManager.saveWarp(warp);
        playSuccessSound(player);
    }

    private void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
    }

    private void playSuccessSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, 1.0f, 1.5f);
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


    private void handleHelp(Player player) {
        messageManager.getComponentList("gui.help").forEach(player::sendMessage);
        playSuccessSound(player);
    }

    private String getDefaultWarpName(UUID owner) {
        return "Warp " + WarpManager.toRoman(warpManager.getPlayerWarpCount(owner) + 1);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return Collections.emptyList();

        UUID uuid = player.getUniqueId();

        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            subcommands.add("help");
            if (player.hasPermission("uwp.user.create")) subcommands.add("create");
            if (player.hasPermission("uwp.user.delete")) subcommands.add("delete");
            if (player.hasPermission("uwp.user.setting")) subcommands.add("setting");
            if (player.hasPermission("uwp.user.trust")) subcommands.add("trust");
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("delete") && player.hasPermission("uwp.user.delete")) {
                List<String> ids = warpManager.getPlayerWarpIds(uuid);
                if (!args[1].isEmpty()) {
                    return ids.stream().filter(id -> id.startsWith(args[1])).collect(Collectors.toList());
                }
                return ids;
            }
            if (args[0].equalsIgnoreCase("setting") && player.hasPermission("uwp.user.setting")) {
                List<String> ids = warpManager.getPlayerWarpIds(uuid);
                if (!args[1].isEmpty()) {
                    return ids.stream().filter(id -> id.startsWith(args[1])).collect(Collectors.toList());
                }
                return ids;
            }
            if (args[0].equalsIgnoreCase("trust") && player.hasPermission("uwp.user.trust")) {
                List<String> ids = warpManager.getPlayerWarpIds(uuid);
                if (!args[1].isEmpty()) {
                    return ids.stream().filter(id -> id.startsWith(args[1])).collect(Collectors.toList());
                }
                return ids;
            }
            if (args[0].equalsIgnoreCase("create") && player.hasPermission("uwp.user.create")) {
                return Collections.singletonList("<warp_id>");
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("trust") && player.hasPermission("uwp.user.trust")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
