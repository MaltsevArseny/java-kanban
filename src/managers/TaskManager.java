package managers;

import tasks.*;
import java.util.List;

public interface TaskManager {
    // Методы для Task
    void addNewTask(Task task);

    Task getTask(int id);

    List<Task> getTasks();

    void updateTask(Task task);

    void removeTask(int id);
    void removeAllTasks();

    // Методы для Subtask
    void addNewSubtask(Subtask subtask);
    Subtask getSubtask(int id);
    List<Subtask> getSubtasks();
    void updateSubtask(Subtask subtask);
    void removeSubtask(int id);
    void removeAllSubtasks();

    // Методы для Epic
    void addNewEpic(Epic epic);
    Epic getEpic(int id);
    List<Epic> getEpics();
    void updateEpic(Epic epic);
    void removeEpic(int id);
    void removeAllEpics();

    // Дополнительные методы
    List<Task> getHistory();
    void updateEpicStatus(int epicId);

    // Новые методы для временных параметров
    List<Task> getPrioritizedTasks();
    boolean hasTimeOverlap(Task task1, Task task2);
    boolean isTimeSlotAvailable(Task task);

    void updateEpicTime(int epicId);
}