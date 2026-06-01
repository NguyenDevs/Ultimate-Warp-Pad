package org.nguyendevs.ultimateWarpPad.manager;

import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.nguyendevs.ultimateWarpPad.model.Warp;

public class WarpParticleManager {

    private final JavaPlugin plugin;
    private final WarpManager warpManager;
    private final ConfigManager configManager;
    private final AnimationManager animationManager;
    private BukkitTask task;

    public WarpParticleManager(JavaPlugin plugin, WarpManager warpManager,
                               ConfigManager configManager, AnimationManager animationManager) {
        this.plugin = plugin;
        this.warpManager = warpManager;
        this.configManager = configManager;
        this.animationManager = animationManager;
    }

    public void start() {
        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!configManager.isParticleEnabled()) return;

                Particle particle = configManager.getParticleType();

                for (Warp warp : warpManager.getAllWarps()) {
                    World world = warp.getWorld();
                    if (world == null) continue;

                    if (animationManager.isAnimating(warp)) continue;

                    if (!hasPlayerNearby(warp)) continue;

                    spawnAmbient(world, warp, particle);
                }
            }
        }.runTaskTimer(plugin, 0L, 15L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private boolean hasPlayerNearby(Warp warp) {
        int wx = (int) Math.floor(warp.getX());
        int wy = (int) Math.floor(warp.getY());
        int wz = (int) Math.floor(warp.getZ());
        int half = warp.isAdminWarp() ? 3 : 2;
        int halfY = warp.isAdminWarp() ? 2 : 1;

        for (Player player : warp.getWorld().getPlayers()) {
            int lx = player.getLocation().getBlockX();
            int ly = player.getLocation().getBlockY();
            int lz = player.getLocation().getBlockZ();
            if (Math.abs(lx - wx) <= half && Math.abs(ly - wy) <= halfY && Math.abs(lz - wz) <= half) {
                return true;
            }
        }
        return false;
    }

    private void spawnAmbient(World world, Warp warp, Particle particle) {
        double cx = Math.floor(warp.getX());
        double cy = Math.floor(warp.getY());
        double cz = Math.floor(warp.getZ());

        boolean isAdmin = warp.isAdminWarp();
        double spread = isAdmin ? 7.0 : 5.0;
        double originXZ = isAdmin ? 3.0 : 2.0;
        double yBase = isAdmin ? cy + 1 : cy;

        for (int i = 0; i < configManager.getIdleParticleAmount(); i++) {
            double x = cx - originXZ + Math.random() * spread;
            double y = yBase + Math.random() * 3;
            double z = cz - originXZ + Math.random() * spread;
            world.spawnParticle(particle, x, y, z, 0, 0, 0.02, 0, 0.01);
        }
    }
}
