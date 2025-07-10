package managers;

import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void shouldCreateFileBackedManager() {
        File file = new File("test.csv");
        FileBackedTaskManager manager = Managers.getFileBackedManager(file);
        assertNotNull(manager);
    }

    @Test
    void shouldCreateInMemoryHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);
    }
}