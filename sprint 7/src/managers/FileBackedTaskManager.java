package managers;

import tasks.*;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return manager;

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                switch (task.getType()) {
                    case TASK -> manager.tasks.put(task.getId(), task);
                    case EPIC -> {
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                    }
                    case SUBTASK -> {
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        manager.epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
                    }
                }
                if (task.getId() >= manager.nextId) {
                    manager.nextId = task.getId() + 1;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file, e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        TaskType type = TaskType.valueOf(parts[1]);
        return switch (type) {
            case TASK, EPIC -> Task.fromCSV(value);
            case SUBTASK -> Subtask.fromCSV(value);
        };
    }

    protected void save() {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("id,type,name,status,description,epic");

            new HashMap<>(tasks).values().forEach(writer::println);
            new HashMap<>(epics).values().forEach(writer::println);
            new HashMap<>(subtasks).values().forEach(writer::println);
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file, e);
        }
    }

    @Override
    public int addNewTask(Task task) {
        int id = super.addNewTask(task);
        save();
        return id;
    }

    // ... (keep all other overridden methods the same)
}