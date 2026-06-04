package org.nguyendevs.ultimateWarpPad.flag;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

public final class UWPFlags {

    public static StateFlag UWP_USE;
    public static StateFlag UWP_PLACE;

    private UWPFlags() {}

    public static void register() {
        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        try {
            UWP_USE = new StateFlag("uwp-use", true);
            registry.register(UWP_USE);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aRegistered WorldGuard flag: uwp-use (default: ALLOW)"));
        } catch (FlagConflictException e) {
            UWP_USE = (StateFlag) registry.get("uwp-use");
        }

        try {
            UWP_PLACE = new StateFlag("uwp-place", false);
            registry.register(UWP_PLACE);
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&3[&bUltimateWarpPad&3] &aRegistered WorldGuard flag: uwp-place (default: DENY)"));
        } catch (FlagConflictException e) {
            UWP_PLACE = (StateFlag) registry.get("uwp-place");
        }
    }

    public static boolean checkFlag(Location location, StateFlag flag) {
        if (flag == null) return true;
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(location));

        StateFlag.State result = null;
        int resultPriority = Integer.MIN_VALUE;
        boolean inCustomRegion = false;

        for (ProtectedRegion region : set.getRegions()) {
            if (region.getId().equals("__global__")) continue;
            inCustomRegion = true;
            StateFlag.State state = region.getFlag(flag);
            if (state != null && region.getPriority() >= resultPriority) {
                result = state;
                resultPriority = region.getPriority();
            }
        }

        if (!inCustomRegion) return true;
        if (result != null) return result == StateFlag.State.ALLOW;
        return flag.getDefault() == StateFlag.State.ALLOW;
    }
}
