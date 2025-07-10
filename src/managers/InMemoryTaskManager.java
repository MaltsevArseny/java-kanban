package managers;

import tasks.*;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = new InMemoryHistoryManager();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    // Методы для Task
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
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (!isTimeSlotAvailable(task)) {
                throw new TimeConflictException("Обновленная задача пересекается по времени");
            }
            prioritizedTasks.remove(tasks.get(task.getId()));
            Task updatedTask = new Task(task.getId(), task.getName(), task.getDescription(),
                    task.getStatus(), task.getDuration(), task.getStartTime());
            tasks.put(task.getId(), updatedTask);
            if (updatedTask.getStartTime() != null) {
                prioritizedTasks.add(updatedTask);
            }
        }
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
        tasks.values().forEach(task -> {
            prioritizedTasks.remove(task);
            historyManager.remove(task.getId());
        });
        tasks.clear();
    }

    // Методы для Subtask
    @Override
    public void addNewSubtask(Subtask subtask) {
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик для подзадачи не найден");
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
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (!isTimeSlotAvailable(subtask)) {
                throw new TimeConflictException("Обновленная подзадача пересекается по времени");
            }
            prioritizedTasks.remove(subtasks.get(subtask.getId()));
            Subtask updatedSubtask = new Subtask(subtask.getId(), subtask.getName(),
                    subtask.getDescription(), subtask.getStatus(),
                    subtask.getDuration(), subtask.getStartTime(),
                    subtask.getEpicId());
            subtasks.put(subtask.getId(), updatedSubtask);
            if (updatedSubtask.getStartTime() != null) {
                prioritizedTasks.add(updatedSubtask);
            }
            updateEpicStatus(updatedSubtask.getEpicId());
        }
    }

    @Override
    public void removeSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    @Override
    public void removeAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask.getId());
            }
        });
        subtasks.clear();
        epics.values().forEach(epic -> updateEpicStatus(epic.getId()));
    }

    // Методы для Epic
    @Override
    public void addNewEpic(Epic epic) {
        Epic newEpic = new Epic(nextId++, epic.getName(), epic.getDescription());
        epics.put(newEpic.getId(), newEpic);
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            Epic updatedEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription());
            updatedEpic.setStatus(existingEpic.getStatus());

            existingEpic.getSubtaskIds().forEach(updatedEpic::addSubtask);

            updatedEpic.setStartTime(existingEpic.getStartTime());
            updatedEpic.setDuration(existingEpic.getDuration());
            epics.put(epic.getId(), updatedEpic);
            updateEpicStatus(epic.getId());
        }
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtaskIds().forEach(subtaskId -> {
                subtasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            });
            historyManager.remove(epic.getId());
        });
        epics.clear();
        subtasks.clear();
    }

    // History methods
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Status and timing methods
    @Override
    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
        }

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
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    @Override
    public boolean isTimeSlotAvailable(Task task) {
        if (task.getStartTime() == null) {
            return true;
        }
        // Упрощенная проверка, так как задачи без времени не попадают в prioritizedTasks
        return prioritizedTasks.stream()
                .noneMatch(t -> hasTimeOverlap(task, t));
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