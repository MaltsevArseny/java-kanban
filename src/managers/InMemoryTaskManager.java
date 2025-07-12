package managers;

import tasks.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected int nextId = 1;
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );

    // Методы для Task
    @Override
    public void addNewTask(Task task) {
        if (task == null) return;
        if (!isTimeSlotAvailable(task)) {
            throw new TimeConflictException("Задача пересекается по времени с существующими");
        }
        task.setId(nextId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
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
        if (task == null || !tasks.containsKey(task.getId())) return;

        Task existingTask = tasks.get(task.getId());
        if (!isTimeSlotAvailable(task, existingTask)) {
            throw new TimeConflictException("Обновленная задача пересекается по времени");
        }

        prioritizedTasks.remove(existingTask);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
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
        if (subtask == null) return;
        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик для подзадачи не найден");
        }
        if (!isTimeSlotAvailable(subtask)) {
            throw new TimeConflictException("Подзадача пересекается по времени");
        }

        subtask.setId(nextId++);
        subtasks.put(subtask.getId(), subtask);
        epics.get(subtask.getEpicId()).addSubtask(subtask.getId());
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        updateEpicTimeAndStatus(subtask.getEpicId());
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
        if (subtask == null || !subtasks.containsKey(subtask.getId())) return;

        Subtask existingSubtask = subtasks.get(subtask.getId());
        if (!isTimeSlotAvailable(subtask, existingSubtask)) {
            throw new TimeConflictException("Обновленная подзадача пересекается по времени");
        }

        prioritizedTasks.remove(existingSubtask);
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }
        updateEpicTimeAndStatus(subtask.getEpicId());
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
                updateEpicTimeAndStatus(epic.getId());
            }
        }
    }

    @Override
    public void removeAllSubtasks() {
        List<Integer> epicIdsToUpdate = subtasks.values().stream()
                .map(Subtask::getEpicId)
                .distinct()
                .toList();

        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });
        subtasks.clear();

        epicIdsToUpdate.forEach(epicId -> {
            Epic epic = epics.get(epicId);
            if (epic != null) {
                epic.getSubtaskIds().clear();
                updateEpicTimeAndStatus(epicId);
            }
        });
    }

    // Методы для Epic
    @Override
    public void addNewEpic(Epic epic) {
        if (epic == null) return;
        epic.setId(nextId++);
        epics.put(epic.getId(), epic);
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
        if (epic == null || !epics.containsKey(epic.getId())) return;

        Epic existingEpic = epics.get(epic.getId());
        Epic updatedEpic = new Epic(epic.getId(), epic.getName(), epic.getDescription());
        updatedEpic.setStatus(existingEpic.getStatus());
        existingEpic.getSubtaskIds().forEach(updatedEpic::addSubtask);
        updatedEpic.setStartTime(existingEpic.getStartTime());
        updatedEpic.setDuration(existingEpic.getDuration());
        epics.put(epic.getId(), updatedEpic);
        updateEpicTimeAndStatus(epic.getId());
    }

    @Override
    public void removeEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                }
                historyManager.remove(subtaskId);
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void removeAllEpics() {
        subtasks.values().forEach(subtask -> {
            prioritizedTasks.remove(subtask);
            historyManager.remove(subtask.getId());
        });
        epics.values().forEach(epic -> historyManager.remove(epic.getId()));
        epics.clear();
        subtasks.clear();
    }

    // Методы для работы с историей
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Методы для работы с временными параметрами
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1 == null || task2 == null ||
                task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }

        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = task1.getEndTime();
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = task2.getEndTime();

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    @Override
    public boolean isTimeSlotAvailable(Task task) {
        if (task == null || task.getStartTime() == null) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .noneMatch(t -> hasTimeOverlap(task, t));
    }

    private boolean isTimeSlotAvailable(Task newTask, Task existingTask) {
        if (newTask == null || newTask.getStartTime() == null) {
            return true;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != existingTask.getId())
                .noneMatch(t -> hasTimeOverlap(newTask, t));
    }

    // Внутренние методы для работы с эпиками
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

    private void updateEpicTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Subtask> epicSubtasks = getEpicSubtasks(epicId);

        if (epicSubtasks.isEmpty()) {
            epic.setStartTime(null);
            epic.setDuration(Duration.ZERO);
            epic.setEndTime(null);
            return;
        }

        LocalDateTime startTime = epicSubtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime endTime = epicSubtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        Duration duration = Duration.ofMinutes(
                epicSubtasks.stream()
                        .mapToLong(s -> s.getDuration().toMinutes())
                        .sum()
        );

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

    private void updateEpicTimeAndStatus(int epicId) {
        updateEpicTime(epicId);
        updateEpicStatus(epicId);
    }

    private List<Subtask> getEpicSubtasks(int epicId) {
        return subtasks.values().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .toList();
    }
}