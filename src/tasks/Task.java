package tasks;

import managers.TaskStatuses;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected TaskStatuses status;

    public Task(String title, String description, TaskStatuses status, int id) {
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

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task with ID " + id ;
    }
}
