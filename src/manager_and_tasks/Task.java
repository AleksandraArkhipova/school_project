package manager_and_tasks;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected String status;

    public Task(String title, String description, String status, int id) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task() {
    }

    public int getId() {

        return id;
    }
}
