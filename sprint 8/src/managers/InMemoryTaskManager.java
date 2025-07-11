package managers;

import tasks.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1; // Счетчик для генерации ID задач
    protected final Map<Integer, Task> tasks = new HashMap<>(); // Хранилище обычных задач
    protected final Map<Integer, Subtask> subtasks = new HashMap<>(); // Хранилище подзадач
    protected final Map<Integer, Epic> epics = new HashMap<>(); // Хранилище эпиков
    protected final HistoryManager historyManager = new InMemoryHistoryManager(); // Менеджер истории

    // Очередь задач, отсортированная по времени начала
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    // Методы для обычных задач
    @Override
    public void addNewTask(Task task) {
        if (!isTimeSlotAvailable(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующими");
        }
        Task newTask = new Task(nextId++, task.getName(), task.getDescription(),
                task.getStatus(), task.getDuration(), task.getStartTime());
        tasks.put(newTask.getId(), newTask);
        if (newTask.getStartTime() != null) {
            prioritizedTasks.add(newTask);
        }
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values()); // Возвращаем копию списка
    }

    @Override
    public void removeTask(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllTasks() {

    }

    // Методы для подзадач
    @Override
    public void addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик не найден");
        }
        if (!isTimeSlotAvailable(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени");
        }
        Subtask newSubtask = new Subtask(nextId++, subtask.getName(), subtask.getDescription(),
                subtask.getStatus(), subtask.getDuration(),
                subtask.getStartTime(), subtask.getEpicId());
        subtasks.put(newSubtask.getId(), newSubtask);
        epics.get(newSubtask.getEpicId()).addSubtask(newSubtask.getId());
        if (newSubtask.getStartTime() != null) {
            prioritizedTasks.add(newSubtask);
        }
        updateEpicStatus(newSubtask.getEpicId());
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

    // Методы для эпиков
    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            Epic updatedEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription());

            // Переносим статус
            updatedEpic.setStatus(existingEpic.getStatus());

            // Копируем подзадачи
            for (Integer subtaskId : existingEpic.getSubtaskIds()) {
                updatedEpic.addSubtask(subtaskId);
            }

            // Переносим временные параметры
            updatedEpic.setStartTime(existingEpic.getStartTime());
            updatedEpic.setDuration(existingEpic.getDuration());

            epics.put(epic.getId(), updatedEpic);
            updateEpicStatus(epic.getId());
        }
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

    // Методы для работы со временем
    @Override
    public boolean isTimeSlotAvailable(Task task) {
        if (task.getStartTime() == null) {
            return true; // Задачи без времени всегда доступны
        }
        return prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null)
                .noneMatch(t -> hasTimeOverlap(task, t));
    }

    // Обновление статуса эпика
    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = epicSubtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.NEW);
        boolean allDone = epicSubtasks.stream().allMatch(s -> s.getStatus() == TaskStatus.DONE);

        if (allNew) {
            epic.setStatus(TaskStatus.NEW);
        } else if (allDone) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.of();
    }

    @Override
    public boolean hasTimeOverlap(Task task1, Task task2) {
        return false;
    }

    private List<Subtask> getEpicSubtasks(int epicId) {
        List<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }
}