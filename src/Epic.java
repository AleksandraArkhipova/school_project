import java.util.ArrayList;

public class Epic extends Task {

    ArrayList<Integer> subtasks = new ArrayList<>();

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
        if (!subtasks.isEmpty()) {
            return subtasks;
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public boolean isSubTasksEmpty() {
        return subtasks.isEmpty();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void clearSubtasksList() {
        subtasks.clear();
    }
}
