import java.util.List;

public interface TaskManager {
    int addNewTask(Task task);
    Task getTask(int id);
    List<Task> getTasks();
    void updateTask(Task task);
    void removeTask(int id);
    void removeAllTasks();

    int addNewSubtask(Subtask subtask);
    Subtask getSubtask(int id);
    List<Subtask> getSubtasks();
    void updateSubtask(Subtask subtask);
    void removeSubtask(int id);
    void removeAllSubtasks();

    void addNewEpic(Epic epic);
    Epic getEpic(int id);
    List<Epic> getEpics();
    void updateEpic(Epic epic);
    void removeEpic(int id);
    void removeAllEpics();

    List<Subtask> getSubtasksForEpic(int epicId);
    List<Task> getHistory();
}
