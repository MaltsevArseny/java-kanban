package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    public LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW, Duration.ZERO, null);
        this.type = TaskType.EPIC;
    }

    public void addSubtask(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

}