public class SubTask extends Task {
    protected int epicId;

    public SubTask(String title, String description, String status, int id, int epicId) {
        super(title, description, status, id);
        this.epicId = epicId;
    }

    public int getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    public String getStatus() {
        return status;
    }
}
