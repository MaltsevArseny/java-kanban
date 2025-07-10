package managers;

import org.junit.jupiter.api.Test;
import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    @Test
    void testGetPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(1, "Task 1", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now.plusHours(2));
        Task task2 = new Task(2, "Task 2", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now);

        manager.addNewTask(task1);
        manager.addNewTask(task2);

        List<Task> prioritized = manager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void testTimeConflictDetection() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(1, "Task 1", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now);

        manager.addNewTask(task1);

        Task task2 = new Task(2, "Task 2", "Desc", TaskStatus.NEW,
                Duration.ofHours(1), now.plusMinutes(30));

        assertThrows(TimeConflictException.class, () -> manager.addNewTask(task2));
    }
}
