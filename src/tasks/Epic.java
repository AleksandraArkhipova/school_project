package tasks;

import managers.TaskStatuses;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String title, String description, int id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public void removeSubTaskById(Integer id) {
        subtasks.remove(id);
    }

    public ArrayList<Integer> getSubTasksList() {

        return subtasks;

    }

    public boolean isSubTasksEmpty() {

        return subtasks.isEmpty();
    }

    public void setStatus(TaskStatuses status) {

        this.status = status;
    }

    public void clearSubtasksList() {

        subtasks.clear();

    }
    @Override
    public String toString() {
        return "Epic with ID " + id ;
    }
}
