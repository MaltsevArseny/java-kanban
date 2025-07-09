package managers;

import tasks.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();

    // Реализация всех методов интерфейса TaskManager
    @Override
    public int addNewTask(Task task) {
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) historyManager.add(task);
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return List.of();
    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void removeTask(int id) {

    }

    @Override
    public void removeAllTasks() {

    }

    @Override
    public void addNewSubtask(Subtask subtask) {
    }

    @Override
    public Subtask getSubtask(int id) {
        return null;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return List.of();
    }

    @Override
    public void updateSubtask(Subtask subtask) {

    }

    @Override
    public void removeSubtask(int id) {

    }

    @Override
    public void removeAllSubtasks() {

    }

    @Override
    public void addNewEpic(Epic epic) {

    }

    @Override
    public Epic getEpic(int id) {
        return null;
    }

    @Override
    public List<Epic> getEpics() {
        return List.of();
    }

    @Override
    public void updateEpic(Epic epic) {

    }

    @Override
    public void removeEpic(int id) {

    }

    @Override
    public void removeAllEpics() {

    }

    @Override
    public List<Task> getHistory() {
        return List.of();
    }

    // ... аналогичные реализации для остальных методов

    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) continue;

            if (subtask.getStatus() != TaskStatus.NEW) allNew = false;
            if (subtask.getStatus() != TaskStatus.DONE) allDone = false;
        }

        if (allNew || epic.getSubtaskIds().isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}