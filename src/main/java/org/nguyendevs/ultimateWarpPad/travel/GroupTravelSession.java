package org.nguyendevs.ultimateWarpPad.travel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.nguyendevs.ultimateWarpPad.manager.AnimationManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;

import java.util.HashSet;
import java.util.Set;

public class GroupTravelSession {

    private final Set<Player> players;
    private final Set<Player> teleported;
    private final Set<Player> landed;
    private final AnimationManager animationManager;
    private final Warp source;
    private final Warp destination;
    private final JavaPlugin plugin;
    private final TravelQueue travelQueue;
    private boolean destAnimStarted;
    private boolean sourceLanded;
    private boolean destLanded;

    public GroupTravelSession(Set<Player> players, Warp source, Warp destination,
                              AnimationManager animationManager, JavaPlugin plugin,
                              TravelQueue travelQueue) {
        this.players = new HashSet<>(players);
        this.teleported = new HashSet<>();
        this.landed = new HashSet<>();
        this.animationManager = animationManager;
        this.source = source;
        this.destination = destination;
        this.plugin = plugin;
        this.travelQueue = travelQueue;
    }

    public boolean shouldStartDestAnimation() {
        if (!destAnimStarted) {
            destAnimStarted = true;
            return true;
        }
        return false;
    }

    public void onPlayerTeleported(Player player) {
        if (sourceLanded) return;
        teleported.add(player);
        if (teleported.containsAll(players)) {
            sourceLanded = true;
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    animationManager.playLandingAnimation(source), 40L);
        }
    }

    public void onPlayerLanded(Player player) {
        if (destLanded) return;
        landed.add(player);
        if (landed.containsAll(players)) {
            destLanded = true;
            animationManager.playLandingAnimation(destination);
            travelQueue.onComplete(destination);
        }
    }

    public void removePlayer(Player player) {
        players.remove(player);
        teleported.remove(player);
        landed.remove(player);
        if (!sourceLanded && !players.isEmpty() && teleported.containsAll(players)) {
            sourceLanded = true;
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    animationManager.playLandingAnimation(source), 40L);
        }
        if (!destLanded && !players.isEmpty() && landed.containsAll(players)) {
            destLanded = true;
            animationManager.playLandingAnimation(destination);
            travelQueue.onComplete(destination);
        }
        if (players.isEmpty()) {
            if (!sourceLanded) {
                sourceLanded = true;
                Bukkit.getScheduler().runTaskLater(plugin, () ->
                        animationManager.playLandingAnimation(source), 40L);
            }
            if (!destLanded) {
                destLanded = true;
                animationManager.playLandingAnimation(destination);
                travelQueue.onComplete(destination);
            }
        }
    }
}
