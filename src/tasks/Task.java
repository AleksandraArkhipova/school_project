package tasks;

import managers.TaskStatuses;

import java.util.Locale;

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

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public int getId() {
        return id;
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
