package managers;

import tasks.*;
import java.util.List;

public interface TaskManager {
    // Методы для Task
    int addNewTask(Task task);
    void getTask(int id);
    List<Task> getTasks();
    void updateTask(Task task);
    void removeTask(int id);
    void removeAllTasks();

    // Методы для Subtask
    int addNewSubtask(Subtask subtask);
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
}