package org.nguyendevs.ultimateWarpPad.travel;

import org.bukkit.entity.Player;
import org.nguyendevs.ultimateWarpPad.model.Warp;

import java.util.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class TravelQueue {

    private final Set<String> busyDestinations = ConcurrentHashMap.newKeySet();
    private final Map<String, Queue<Entry>> queues = new ConcurrentHashMap<>();
    private final Consumer<Entry> onReady;

    public record Entry(Player player, Warp source, Warp destination) {}

    public TravelQueue(Consumer<Entry> onReady) {
        this.onReady = onReady;
    }

    public boolean isBusy(Warp destination) {
        return busyDestinations.contains(destination.getCompositeId());
    }

    public boolean tryClaim(Warp destination) {
        return busyDestinations.add(destination.getCompositeId());
    }

    public void enqueue(Player player, Warp source, Warp destination) {
        String destId = destination.getCompositeId();
        queues.computeIfAbsent(destId, k -> new LinkedList<>())
              .add(new Entry(player, source, destination));
    }

    public boolean isQueued(Player player) {
        UUID uuid = player.getUniqueId();
        for (Queue<Entry> queue : queues.values()) {
            for (Entry entry : queue) {
                if (entry.player().getUniqueId().equals(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void onComplete(Warp destination) {
        String destId = destination.getCompositeId();
        Queue<Entry> queue = queues.get(destId);
        if (queue != null) {
            Entry next;
            while ((next = queue.poll()) != null) {
                if (!next.player().isOnline()) continue;
                onReady.accept(next);
                return;
            }
        }
        queues.remove(destId);
        busyDestinations.remove(destId);
    }
}
