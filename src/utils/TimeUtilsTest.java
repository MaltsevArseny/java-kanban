package utils;

import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class TimeUtilsTest {
    @Test
    void testHasTimeOverlap() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(1, "Task 1", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now);
        Task task2 = new Task(2, "Task 2", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now.plusMinutes(30));

        assertTrue(TimeUtils.hasTimeOverlap(task1, task2));
    }
}
