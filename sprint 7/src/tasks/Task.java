package tasks;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType type;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.type = TaskType.TASK;
    }

    // Геттеры, которые используются в других классах
    public int getId() { return id; }
    public TaskStatus getStatus() { return status; }
    public TaskType getType() { return type; }

    // Сеттеры, которые используются в других классах
    public void setId(int id) { this.id = id; }
    public void setStatus(TaskStatus status) { this.status = status; }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,", id, type, name, status, description);
    }

    public static Task fromCSV(String csvLine) {
        String[] values = csvLine.split(",");
        int id = Integer.parseInt(values[0]);
        String name = values[2];
        TaskStatus status = TaskStatus.valueOf(values[3]);
        String description = values[4];
        return new Task(id, name, description, status);
    }
}