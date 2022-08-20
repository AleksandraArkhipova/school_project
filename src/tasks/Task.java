package tasks;

import managers.TaskStatuses;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected TaskStatuses status;
    protected TaskTypes type;

    public Task(String title, TaskTypes type, String description, TaskStatuses status, int id) {
        this.title = title;
        this.type = type;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task() {
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String type = this.getClass().toString().toUpperCase()
                .replace("CLASS TASKS.", "");

        return id + "," + type + "," + title + "," + status + "," + description + ",";
    }
}
