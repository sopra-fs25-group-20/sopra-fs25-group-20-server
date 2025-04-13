package ch.uzh.ifi.hase.soprafs25.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class GameTimerService {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    /**
     * Schedule a task to run after a delay in seconds.
     * @param key A unique key (e.g., "roomCode-vote" or "roomCode-round")
     * @param task The task to run
     * @param delaySeconds Delay before execution
     */
    public void schedule(String key, Runnable task, int delaySeconds) {
        cancel(key); // prevent duplicate schedules

        ScheduledFuture<?> future = scheduler.schedule(task, delaySeconds, TimeUnit.SECONDS);
        scheduledTasks.put(key, future);
    }

    public void cancel(String key) {
        ScheduledFuture<?> future = scheduledTasks.remove(key);
        if (future != null && !future.isDone()) {
            future.cancel(false); // do not interrupt if running
        }
    }

    public boolean isScheduled(String key) {
        ScheduledFuture<?> future = scheduledTasks.get(key);
        return future != null && !future.isDone();
    }

    public void cancelAll() {
        scheduledTasks.keySet().forEach(this::cancel);
    }
}
