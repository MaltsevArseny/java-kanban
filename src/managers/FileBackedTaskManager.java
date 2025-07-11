package managers;

import tasks.*;
import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.load();
        return manager;
    }

    private void load() {
        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return;

            for (int i = 1; i < lines.length; i++) {
                Task task = fromString(lines[i]);
                if (task == null) continue;

                switch (task) {
                    case Epic epic -> epics.put(epic.getId(), epic);
                    case Subtask subtask -> {
                        subtasks.put(subtask.getId(), subtask);
                        if (epics.containsKey(subtask.getEpicId())) {
                            epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
                        }
                        if (subtask.getStartTime() != null) {
                            prioritizedTasks.add(subtask);
                        }
                    }
                    default -> {}
                }

                if (task.getId() >= nextId) {
                    nextId = task.getId() + 1;
                }
            }
            updateAllEpics();
        } catch (IOException e) {
            throw new ManagerSaveException("Can't read from file: " + file, e);
        }
    }

    protected void save() {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("id,type,name,status,description,epic,duration,startTime");

            tasks.values().forEach(task -> writer.println(toCSV(task)));
            epics.values().forEach(epic -> writer.println(toCSV(epic)));
            subtasks.values().forEach(subtask -> writer.println(toCSV(subtask)));
        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file, e);
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        if (parts.length < 8) return null;

        try {
            int id = Integer.parseInt(parts[0]);
            String typeStr = parts[1];
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts[4];
            Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));
            LocalDateTime startTime = parts[7].equals("null") ? null : LocalDateTime.parse(parts[7]);

            return switch (typeStr) {
                case "TASK" -> new Task(id, name, description, status, duration, startTime);
                case "EPIC" -> new Epic(id, name, description);
                case "SUBTASK" -> {
                    int epicId = Integer.parseInt(parts[5]);
                    yield new Subtask(id, name, description, status, duration, startTime, epicId);
                }
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private String toCSV(Task task) {
        String epicId = task instanceof Subtask subtask ? String.valueOf(subtask.getEpicId()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%d,%s",
                task.getId(),
                task.getClass().getSimpleName().toUpperCase(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                task.getDuration().toMinutes(),
                task.getStartTime() != null ? task.getStartTime() : "null");
    }

    @Override
    public void addNewTask(Task task) {
        if (isTimeSlotAvailable(task)) {
            throw new TimeConflictException("Task time conflicts with existing tasks");
        }
        int id = nextId++;
        Task newTask = new Task(id, task.getName(), task.getDescription(),
                task.getStatus(), task.getDuration(), task.getStartTime());
        tasks.put(id, newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
        save();
    }

    private void updateAllEpics() {
        for (Epic epic : epics.values()) {
            updateEpicTime(epic.getId());
            updateEpicStatus(epic.getId());
        }
    }
}