package tasks;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
        this.type = TaskType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    // Удален неиспользуемый метод removeSubtask()

    @Override
    public String toString() {
        return String.format("Epic{id=%d, name='%s', description='%s', status=%s, subtaskIds=%s}",
                id, name, description, status, subtaskIds);
    }

    // Удален переопределенный метод toCSV(), так как он идентичен родительскому
}