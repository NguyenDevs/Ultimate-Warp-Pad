package org.nguyendevs.ultimateWarpPad.travel;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.nguyendevs.ultimateWarpPad.UltimateWarpPad;
import org.nguyendevs.ultimateWarpPad.manager.AnimationManager;
import org.nguyendevs.ultimateWarpPad.manager.ConfigManager;
import org.nguyendevs.ultimateWarpPad.manager.MessageManager;
import org.nguyendevs.ultimateWarpPad.model.Warp;

import java.util.Map;
import java.util.Objects;

public class TravelTask extends BukkitRunnable {

    private final Player player;
    private final Warp destination;
    private final ConfigManager config;
    private final AnimationManager animationManager;
    private final MessageManager messageManager;
    private final Warp source;
    private final JavaPlugin plugin;
    private final GroupTravelSession session;
    private final TravelQueue travelQueue;
    private final double relOffsetX;
    private final double relOffsetZ;

    private int ticks;
    private boolean phase2;
    private boolean teleported;
    private boolean landed;
    private boolean destAnimStarted;
    private int convergeTicks;
    private int spiralTicks;

    public TravelTask(JavaPlugin plugin, Player player, Warp source, Warp destination,
                      ConfigManager config, AnimationManager animationManager,
                      MessageManager messageManager, TravelQueue travelQueue, double relOffsetX, double relOffsetZ) {
        this(plugin, player, source, destination, config, animationManager, messageManager, null, travelQueue, relOffsetX, relOffsetZ);
    }

    public TravelTask(JavaPlugin plugin, Player player, Warp source, Warp destination,
                      ConfigManager config, AnimationManager animationManager,
                      MessageManager messageManager, GroupTravelSession session,
                      TravelQueue travelQueue, double relOffsetX, double relOffsetZ) {
        this.plugin = plugin;
        this.player = player;
        this.source = source;
        this.destination = destination;
        this.config = config;
        this.animationManager = animationManager;
        this.messageManager = messageManager;
        this.session = session;
        this.travelQueue = Objects.requireNonNull(travelQueue);
        this.relOffsetX = relOffsetX;
        this.relOffsetZ = relOffsetZ;
        this.ticks = 0;
        this.phase2 = false;
        this.teleported = false;
        this.landed = false;
        this.destAnimStarted = false;
        this.convergeTicks = 0;
        this.spiralTicks = 0;
    }

    @Override
    public void run() {
        if (!player.isOnline()) {
            if (session != null) {
                session.removePlayer(player);
            }
            cleanup();
            return;
        }

        if (!teleported) {
            int bx = (int) Math.floor(source.getX());
            int bz = (int) Math.floor(source.getZ());
            if (Math.abs(player.getLocation().getBlockX() - bx) > 1
                    || Math.abs(player.getLocation().getBlockZ() - bz) > 1) {
                player.removePotionEffect(PotionEffectType.LEVITATION);
                player.removePotionEffect(PotionEffectType.DARKNESS);
                player.removePotionEffect(PotionEffectType.INVISIBILITY);
                player.removePotionEffect(PotionEffectType.GLOWING);
                UltimateWarpPad.FALL_DAMAGE_IMMUNE.remove(player.getUniqueId());
                messageManager.send(player, "travel.cancelled");
                playCancelSound();
                if (session != null) {
                    session.removePlayer(player);
                } else {
                    animationManager.playLandingAnimation(source);
                }
                if (session == null) travelQueue.onComplete(destination);
                this.cancel();
                return;
            }

            if (!destAnimStarted) {
                double halfY = Math.floor(source.getY()) + 1 + config.getLaunchY() / 2.0;
                if (player.getY() >= halfY) {
                    destAnimStarted = true;
                    boolean shouldPlay = session == null || session.shouldStartDestAnimation();
                    if (shouldPlay) {
                        animationManager.playAnimation(destination);
                    }
                }
            }

            if (config.isForceStay() && ticks % 4 == 0) {
                double targetX, targetZ;
                if (config.isCenter()) {
                    targetX = Math.floor(source.getX()) + 0.5;
                    targetZ = Math.floor(source.getZ()) + 0.5;
                } else {
                    targetX = Math.floor(source.getX()) + 0.5 + relOffsetX;
                    targetZ = Math.floor(source.getZ()) + 0.5 + relOffsetZ;
                }
                Location loc = player.getLocation();
                if (Math.abs(loc.getX() - targetX) > 0.1 || Math.abs(loc.getZ() - targetZ) > 0.1) {
                    loc.setX(targetX);
                    loc.setZ(targetZ);
                    player.teleport(loc);
                }
            }

        }

        if (config.isParticleEnabled() && (ticks & 1) == 0) {
            if (!teleported) {
                spawnConvergeParticles();
                if (phase2) {
                    spawnSpiralParticles();
                }
            }
        }

        ticks++;

        if (!phase2 && ticks >= 50) {
            phase2 = true;
            player.removePotionEffect(PotionEffectType.LEVITATION);
            player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 60, false, false));
            Location ploc = player.getLocation();
            player.getWorld().playSound(ploc, "minecraft:block.portal.ambient", SoundCategory.AMBIENT, 0.8f, 0.1f);
            player.getWorld().playSound(ploc, "minecraft:ambient.crimson_forest.loop", SoundCategory.AMBIENT, 1.0f, 1.0f);

        }

        if (!teleported && player.getY() >= Math.floor(source.getY()) + 2 + config.getLaunchY()) {
            teleportPlayer();
        }

        if (teleported && !landed) {
            if (config.isForceStay() && ticks % 4 == 0) {
                double targetX, targetZ;
                if (config.isCenter()) {
                    targetX = Math.floor(destination.getX()) + 0.5;
                    targetZ = Math.floor(destination.getZ()) + 0.5;
                } else {
                    targetX = Math.floor(destination.getX()) + 0.5 + relOffsetX;
                    targetZ = Math.floor(destination.getZ()) + 0.5 + relOffsetZ;
                }
                Location loc = player.getLocation();
                if (Math.abs(loc.getX() - targetX) > 0.1 || Math.abs(loc.getZ() - targetZ) > 0.1) {
                    loc.setX(targetX);
                    loc.setZ(targetZ);
                    player.teleport(loc);
                }
            }

            if (player.isOnGround() || ticks > 600) {
                landPlayer();
            } else if (config.isAllowDamageCancel() && player.getHealth() < player.getHealthScale()) {
                onDamageTaken();
            }
        }

        if (ticks > 600) {
            if (session != null) {
                session.removePlayer(player);
            }
            if (session == null) travelQueue.onComplete(destination);
            cleanup();
        }
    }

    private void spawnConvergeParticles() {
        World world = source.getWorld();
        if (world == null) return;

        double cx = Math.floor(source.getX()) + 0.5;
        double cz = Math.floor(source.getZ()) + 0.5;
        double cy = Math.floor(source.getY()) + 0.5;
        Particle particle = config.getParticleType();

        convergeTicks++;
        double radius = Math.max(0.2, 3.5 - convergeTicks * 0.12);

        for (int i = 0; i < config.getTriggerParticleAmount(); i++) {
            double angle = Math.random() * 2 * Math.PI;
            double x = cx + Math.cos(angle) * radius;
            double z = cz + Math.sin(angle) * radius;
            double y = cy + Math.random() * 3.0;
            world.spawnParticle(particle, x, y, z, 0, (cx - x) * 0.15, 0.01, (cz - z) * 0.15, 0.5);
        }

        if (convergeTicks % 4 == 0) {
            for (int i = 0; i < config.getTriggerParticleAmount(); i++) {
                double angle = Math.random() * 2 * Math.PI;
                double r = 2.5 + Math.random() * 1.0;
                double y = cy + Math.random() * 3.0;
                world.spawnParticle(particle, cx + Math.cos(angle) * r, y, cz + Math.sin(angle) * r, 0, 0, 0.04, 0, 0.2);
            }
        }
    }

    private void spawnSpiralParticles() {
        World world = source.getWorld();
        if (world == null) return;

        double cx = Math.floor(source.getX()) + 0.5;
        double cz = Math.floor(source.getZ()) + 0.5;
        double cy = Math.floor(source.getY()) + 0.5;
        Particle particle = config.getParticleType();

        spiralTicks++;
        double maxHeight = Math.min(spiralTicks * 0.3, 5.0);

        for (double h = 0; h < maxHeight; h += 1.0) {
            double progress = h / 5.0;
            double radius = 0.3 + progress * 2.2;
            double angle = spiralTicks * 0.4 + h * 1.2;

            world.spawnParticle(particle,
                    cx + Math.cos(angle) * radius,
                    cy + h,
                    cz + Math.sin(angle) * radius,
                    0, Math.cos(angle) * 0.15, 0.05, Math.sin(angle) * 0.15, 0.2);
        }
    }

    private void teleportPlayer() {
        player.removePotionEffect(PotionEffectType.LEVITATION);

        Location destLoc = destination.getLocation().clone();
        if (config.isCenter()) {
            destLoc.setX(Math.floor(destLoc.getX()) + 0.5);
            destLoc.setZ(Math.floor(destLoc.getZ()) + 0.5);
        } else {
            destLoc.setX(Math.floor(destLoc.getX()) + 0.5 + relOffsetX);
            destLoc.setZ(Math.floor(destLoc.getZ()) + 0.5 + relOffsetZ);
        }
        destLoc.setY(Math.floor(destLoc.getY()) + 2 + config.getLaunchY());

        destLoc.setYaw(player.getLocation().getYaw());
        destLoc.setPitch(player.getLocation().getPitch());

        player.teleport(destLoc);
        UltimateWarpPad.FALL_DAMAGE_IMMUNE.add(player.getUniqueId());

        teleported = true;

        if (session != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    session.onPlayerTeleported(player), 40L);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    animationManager.playLandingAnimation(source), 40L);
        }
    }

    private void landPlayer() {
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.GLOWING);
        UltimateWarpPad.FALL_DAMAGE_IMMUNE.remove(player.getUniqueId());
        landed = true;
        if (session != null) {
            session.onPlayerLanded(player);
        } else {
            animationManager.playLandingAnimation(destination);
        }
        messageManager.send(player, "travel.arrived",
                Map.of("destination", destination.getWarpName()));
        if (session == null) travelQueue.onComplete(destination);
        this.cancel();
    }

    private void playCancelSound() {
        source.getWorld().playSound(source.getLocation(), "minecraft:block.beacon.deactivate", SoundCategory.AMBIENT, 1.0f, 0.8f);
    }

    private void onDamageTaken() {
        player.removePotionEffect(PotionEffectType.LEVITATION);
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.GLOWING);
        messageManager.send(player, "travel.cancelled");
        playCancelSound();
        if (session != null) {
            session.removePlayer(player);
        }
        if (session == null) travelQueue.onComplete(destination);
        cleanup();
    }

    private void cleanup() {
        if (!landed) {
            UltimateWarpPad.FALL_DAMAGE_IMMUNE.remove(player.getUniqueId());
            messageManager.send(player, "travel.cancelled");
            playCancelSound();
            if (session == null) travelQueue.onComplete(destination);
        }
        player.removePotionEffect(PotionEffectType.DARKNESS);
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.removePotionEffect(PotionEffectType.GLOWING);
        if (session == null) {
            animationManager.cancelAnimation(source);
        }
        this.cancel();
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();
    }
}
