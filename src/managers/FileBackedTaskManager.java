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
                    case EPIC -> manager.epics.put(task.getId(), (Epic) task);
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
            case TASK -> Task.fromCSV(value);
            case EPIC -> Epic.fromCSV(value);
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

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public int addNewSubtask(Subtask subtask) {
        int id = super.addNewSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public void removeAllSubtasks() {
        super.removeAllSubtasks();
        save();
    }

    @Override
    public void addNewEpic(Epic epic) {
        super.addNewEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void updateEpicStatus(int epicId) {
        super.updateEpicStatus(epicId);
        save();
    }
}