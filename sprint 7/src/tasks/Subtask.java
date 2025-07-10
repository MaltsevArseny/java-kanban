package tasks;

public class Subtask extends Task {
    private final int epicId;  // Сделали поле final, так как оно не изменяется

    public Subtask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
        this.type = TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toCSV() {
        return String.format("%d,%s,%s,%s,%s,%d",
                id, type, name, status, description, epicId);
    }

    public static Subtask fromCSV(String csv) {
        String[] values = csv.split(",");
        return new Subtask(
                Integer.parseInt(values[0]),
                values[2],
                values[4],
                TaskStatus.valueOf(values[3]),
                Integer.parseInt(values[5])
        );
    }

    @Override
    public String toString() {
        return String.format("Subtask{id=%d, name='%s', description='%s', status=%s, epicId=%d}",
                id, name, description, status, epicId);
    }
}