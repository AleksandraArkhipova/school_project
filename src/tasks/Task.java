package tasks;

import managers.TaskStatuses;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    protected String title;
    protected String description;
    protected int id;
    protected TaskStatuses status;
    protected TaskTypes type;
    protected Duration duration;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;


    public Task(String title, String description, TaskStatuses status, Duration duration, LocalDateTime startTime) {
        this.title = title;
        this.type = TaskTypes.TASK;
        this.description = description;
        this.status = status;
        this.id = 0;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task() {
    }

    public void setStatus(TaskStatuses status) {
        this.status = status;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setEndTimeForTaskOrSubtask() {
        if (startTime != null && duration != null) {
            this.endTime = startTime.plusMinutes(duration.toMinutes());
        } else {
            this.endTime = LocalDateTime.MAX;
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public TaskTypes getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + "," + type + "," + title + "," + status + "," + description
                + "," + duration + "," + startTime + "," + endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(title, task.title) && Objects.equals(description, task.description) && status == task.status && type == task.type && Objects.equals(duration, task.duration) && Objects.equals(startTime, task.startTime) && Objects.equals(endTime, task.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, type, duration, startTime, endTime);
    }
}
