package ch.uzh.ifi.hase.soprafs25.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Service
public class GameTimerService {

    private static final Logger log = LoggerFactory.getLogger(GameTimerService.class);
    private final Map<String, ScheduledFuture<?>> activeScheduledTasks = new ConcurrentHashMap<>();
    private final Map<String, Instant> timerEndInstants = new ConcurrentHashMap<>();

    private final TaskScheduler taskScheduler;

    public GameTimerService(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    public void scheduleTask(String timerId, long durationInSeconds, Runnable taskToExecute) {
        // To avoid duplicates
        cancelTask(timerId, "A new task is being scheduled with the same ID: " + timerId);

        Instant executionTime = Instant.now().plusSeconds(durationInSeconds);
        timerEndInstants.put(timerId, executionTime);

        // Also wrapping the original task
        Runnable completeTaskWrapper = () -> {
            try {
                taskToExecute.run();
            } finally {
                activeScheduledTasks.remove(timerId);
                log.debug("Timer task for ID '{}' completed and removed from active list.", timerId);
            }
        };

        ScheduledFuture<?> scheduledTask = taskScheduler.schedule(completeTaskWrapper, executionTime);
        activeScheduledTasks.put(timerId, scheduledTask);
        log.info("Task scheduled for ID: '{}'. Will execute at: {}.", timerId, executionTime);
    }

    public void cancelTask(String timerId, String reason) {
        // remove() also returns the timer
        ScheduledFuture<?> timer = activeScheduledTasks.remove(timerId);
        timerEndInstants.remove(timerId);
        if (timer != null) {
            boolean wasCancelled = timer.cancel(true); // true: allows interrupting even if it runs
            log.info("Canceled timer for ID: '{}'. Success: {}. Reason: {}", timerId, wasCancelled, reason);
        } else {
            log.info("No active timer found for ID: '{}' to cancel. Reason: {}", timerId, reason);
        }
    }

    public boolean isTimerActive(String timerId) {
        return activeScheduledTasks.containsKey(timerId);
    }

    public Optional<Long> getRemainingSeconds(String timerId) {
        Instant endTime = timerEndInstants.get(timerId);
        if (endTime == null) {
            return Optional.empty();
        }
        long remaining = Duration.between(Instant.now(), endTime).toSeconds();
        return Optional.of(Math.max(0L, remaining));
    }
}