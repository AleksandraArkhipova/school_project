package tasks;

import managers.TaskStatuses;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private final int epicId;

    public SubTask(String title, String description, TaskStatuses status,
                   Duration duration, LocalDateTime startTime, int epicId) {
        super(title, description, status, duration, startTime );
        this.id = 0;
        this.epicId = epicId;
        this.type = TaskTypes.SUBTASK;
    }

    public int getEpicId() {

        return epicId;
    }

    public TaskStatuses getStatus() {

        return status;
    }

    @Override
    public String toString() {

        return super.toString() + "," + epicId;
    }
}
