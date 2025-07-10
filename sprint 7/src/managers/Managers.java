package managers;

import java.io.File;

public class Managers {
    public static FileBackedTaskManager getFileBackedManager(File file) {
        return new FileBackedTaskManager(file);
    }
}
