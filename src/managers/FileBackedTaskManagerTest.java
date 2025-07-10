package managers;

import org.junit.jupiter.api.*;
import tasks.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        if (!tempFile.delete()) {
            System.err.println("Failed to delete temporary file: " + tempFile.getAbsolutePath());
        }
    }

    @Test
    void shouldSaveAndLoadEmptyManager() {
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertAll(
                () -> assertTrue(loaded.getTasks().isEmpty()),
                () -> assertTrue(loaded.getEpics().isEmpty()),
                () -> assertTrue(loaded.getSubtasks().isEmpty())
        );
    }

    @Test
    void shouldSaveAndLoadTasks() {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW);
        manager.addNewTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loaded.getTasks();

        assertAll(
                () -> assertEquals(1, tasks.size()),
                () -> assertEquals(task, tasks.getFirst())
        );
    }

    @Test
    void shouldSaveAndLoadEpicsWithSubtasks() {
        Epic epic = new Epic(1, "Epic", "Description");
        manager.addNewEpic(epic);

        Subtask subtask = new Subtask(2, "Subtask", "Description", TaskStatus.NEW, epic.getId());
        manager.addNewSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertAll(
                () -> assertEquals(1, loaded.getEpics().size()),
                () -> assertEquals(1, loaded.getSubtasks().size()),
                () -> assertEquals(epic.getId(), loaded.getSubtask(2).getEpicId())
        );
    }

    @Test
    void shouldSaveHistory() {
        Task task = new Task(1, "Task", "Description", TaskStatus.NEW);
        manager.addNewTask(task);
        manager.getTask(1); // Добавляем в историю

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        loaded.getTask(1); // Загружаем историю

        assertEquals(1, loaded.getHistory().size());
    }

    @Test
    void shouldThrowExceptionWhenFileInvalid() {
        assertThrows(ManagerSaveException.class, () -> {
            File invalidFile = new File("invalid/path/tasks.csv");
            FileBackedTaskManager.loadFromFile(invalidFile);
        });
    }
}
