package tasks;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String title, TaskTypes type, String description, int id) {
        this.title = title;
        this.type = type;
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

    public boolean isContainItems() {

        return !subtasks.isEmpty();
    }

    public void clearSubtasksList() {

        subtasks.clear();

    }

}
