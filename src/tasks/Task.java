package tasks;

import managers.TaskStatuses;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected TaskStatuses status;
    protected TaskTypes type;

    public Task(String title, String description, TaskStatuses status, int id) {
        this.title = title;
        this.type = TaskTypes.TASK;
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

        return id + "," + type.toString() + "," + title + "," + status + "," + description + ",";
    }
}
