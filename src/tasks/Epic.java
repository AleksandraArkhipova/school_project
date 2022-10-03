package tasks;

import managers.TaskStatuses;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class  Epic extends Task {

    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String title, String description) {
        super();
        this.status = TaskStatuses.NEW;
        this.title = title;
        this.type = TaskTypes.EPIC;
        this.description = description;
        this.id = 0;
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public void removeSubTaskById(Integer id) {
        subtasks.remove(id);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public TaskStatuses getStatus() {
        return status;
    }
    public ArrayList<Integer> getSubTasksList() {

        return subtasks;

    }

    public boolean isContainSubtasks() {

        return !subtasks.isEmpty();
    }

    public void clearSubtasksList() {

        subtasks.clear();

    }

}
