public class Subtask extends Task {
    private int epicId;

    public Subtask(int id, String name, int epicId) {
        super(id, name);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}