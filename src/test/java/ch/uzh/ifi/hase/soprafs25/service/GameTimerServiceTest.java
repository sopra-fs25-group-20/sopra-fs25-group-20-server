package ch.uzh.ifi.hase.soprafs25.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameTimerServiceTest {

    private TaskScheduler taskScheduler;
    private GameTimerService timerService;

    @BeforeEach
    void setup() {
        taskScheduler = mock(TaskScheduler.class);
        timerService = new GameTimerService(taskScheduler);
    }

    @Test
    void testScheduleTask_addsAndRemovesCorrectly() {
        Runnable task = mock(Runnable.class);
        @SuppressWarnings("unchecked")
        ScheduledFuture<Object> future = mock(ScheduledFuture.class);
        when((ScheduledFuture<Object>) taskScheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(future);

        String id = "test_timer";
        timerService.scheduleTask(id, 1, task);

        assertTrue(timerService.isTimerActive(id));
    }

    @Test
    void testCancelTask_existingTask() {
        Runnable task = mock(Runnable.class);
        @SuppressWarnings("unchecked")
        ScheduledFuture<Object> future = mock(ScheduledFuture.class);
        when((ScheduledFuture<Object>) taskScheduler.schedule(any(Runnable.class), any(Instant.class))).thenReturn(future);
        when(future.cancel(true)).thenReturn(true);

        String id = "cancel_me";
        timerService.scheduleTask(id, 5, task);
        timerService.cancelTask(id, "test reason");

        assertFalse(timerService.isTimerActive(id));
    }

    @Test
    void testCancelTask_noExistingTimer() {
        timerService.cancelTask("non_existent", "no-op");
    }

    @Test
    void testGetRemainingSeconds_returnsCorrectly() throws Exception {
        String id = "remain_timer";
        Instant futureTime = Instant.now().plusSeconds(10);

        var endTimeField = GameTimerService.class.getDeclaredField("timerEndInstants");
        endTimeField.setAccessible(true);
        @SuppressWarnings("unchecked")
        var map = (java.util.Map<String, Instant>) endTimeField.get(timerService);
        map.put(id, futureTime);

        Optional<Long> remaining = timerService.getRemainingSeconds(id);
        assertTrue(remaining.isPresent());
        assertTrue(remaining.get() <= 10 && remaining.get() > 0);
    }

    @Test
    void testGetRemainingSeconds_emptyWhenMissing() {
        assertTrue(timerService.getRemainingSeconds("none").isEmpty());
    }
}
