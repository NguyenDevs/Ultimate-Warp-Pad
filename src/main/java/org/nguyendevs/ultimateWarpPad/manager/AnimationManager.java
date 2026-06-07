package org.nguyendevs.ultimateWarpPad.manager;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.nguyendevs.ultimateWarpPad.model.Warp;
import org.nguyendevs.ultimateWarpPad.schematic.AdminWarpSchematicData;
import org.nguyendevs.ultimateWarpPad.schematic.PlayerWarpSchematicData;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnimationManager {

    private static final int MAX_CONCURRENT = 15;
    private static final int[][] START_SEQUENCE = {
            {2, 10},
            {3, 10},
    };
    private static final int IDLE_SCHEMATIC = 4;
    private static final int[][] LANDING_SEQUENCE = {
            {5, 10},
            {6, 10},
            {1, 10},
    };

    private final JavaPlugin plugin;
    private final Map<String, BukkitTask> activeAnimations;
    private final Map<String, BukkitTask> landingAnimations;
    private final Queue<Warp> animationQueue;
    private int activeCount;

    public AnimationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.activeAnimations = new ConcurrentHashMap<>();
        this.landingAnimations = new ConcurrentHashMap<>();
        this.animationQueue = new LinkedList<>();
    }

    public void playAnimation(Warp warp) {
        cancelAnimation(warp);

        if (activeCount >= MAX_CONCURRENT) {
            animationQueue.add(warp);
            return;
        }

        startAnimation(warp);
    }

    private void startAnimation(Warp warp) {
        World world = warp.getWorld();
        if (world == null) {
            processQueue();
            return;
        }

        activeCount++;

        boolean isAdmin = warp.isAdminWarp();
        int ox = (int) Math.floor(warp.getX()) - (isAdmin ? 3 : 2);
        int oy = (int) Math.floor(warp.getY()) - (isAdmin ? 1 : 2);
        int oz = (int) Math.floor(warp.getZ()) - (isAdmin ? 3 : 2);

        BukkitTask task = new BukkitRunnable() {
            int phase = 0;
            int tick = 0;

            @Override
            public void run() {
                if (phase >= START_SEQUENCE.length) {
                    if (isAdmin) {
                        AdminWarpSchematicData.paste(world, ox, oy, oz, IDLE_SCHEMATIC - 1);
                    } else {
                        PlayerWarpSchematicData.paste(world, ox, oy, oz, IDLE_SCHEMATIC - 1);
                    }
                    activeAnimations.remove(warp.getCompositeId());
                    activeCount--;
                    processQueue();
                    this.cancel();
                    return;
                }

                int[] seq = START_SEQUENCE[phase];
                int schematicIndex = seq[0] - 1;
                int duration = seq[1];

                if (tick == 0) {
                    if (isAdmin) {
                        AdminWarpSchematicData.paste(world, ox, oy, oz, schematicIndex);
                    } else {
                        PlayerWarpSchematicData.paste(world, ox, oy, oz, schematicIndex);
                    }
                }

                tick++;

                if (tick >= duration) {
                    phase++;
                    tick = 0;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        activeAnimations.put(warp.getCompositeId(), task);
    }

    private void processQueue() {
        while (activeCount < MAX_CONCURRENT && !animationQueue.isEmpty()) {
            Warp warp = animationQueue.poll();
            startAnimation(warp);
        }
    }

    public void playLandingAnimation(Warp warp) {
        String id = warp.getCompositeId();

        cancelAnimation(warp);

        BukkitTask existing = landingAnimations.get(id);
        if (existing != null) existing.cancel();

        World world = warp.getWorld();
        if (world == null) return;

        boolean isAdmin = warp.isAdminWarp();
        int ox = (int) Math.floor(warp.getX()) - (isAdmin ? 3 : 2);
        int oy = (int) Math.floor(warp.getY()) - (isAdmin ? 1 : 2);
        int oz = (int) Math.floor(warp.getZ()) - (isAdmin ? 3 : 2);

        BukkitTask task = new BukkitRunnable() {
            int phase = 0;
            int tick = 0;

            @Override
            public void run() {
                if (phase >= LANDING_SEQUENCE.length) {
                    landingAnimations.remove(id);
                    this.cancel();
                    return;
                }

                int[] seq = LANDING_SEQUENCE[phase];
                int schematicIndex = seq[0] - 1;
                int duration = seq[1];

                if (tick == 0) {
                    if (isAdmin) {
                        AdminWarpSchematicData.paste(world, ox, oy, oz, schematicIndex);
                    } else {
                        PlayerWarpSchematicData.paste(world, ox, oy, oz, schematicIndex);
                    }
                }

                tick++;

                if (tick >= duration) {
                    phase++;
                    tick = 0;
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        landingAnimations.put(id, task);
    }

    public void cancelAnimation(Warp warp) {
        String id = warp.getCompositeId();
        BukkitTask task = activeAnimations.remove(id);
        if (task != null) {
            task.cancel();
            activeCount--;
            processQueue();
        }
        animationQueue.removeIf(w -> w.getCompositeId().equals(id));
    }

    public boolean isAnimating(Warp warp) {
        return activeAnimations.containsKey(warp.getCompositeId());
    }

    public void cancelAll() {
        for (Map.Entry<String, BukkitTask> entry : activeAnimations.entrySet()) {
            entry.getValue().cancel();
        }
        for (Map.Entry<String, BukkitTask> entry : landingAnimations.entrySet()) {
            entry.getValue().cancel();
        }
        activeAnimations.clear();
        landingAnimations.clear();
        animationQueue.clear();
        activeCount = 0;
    }
}
