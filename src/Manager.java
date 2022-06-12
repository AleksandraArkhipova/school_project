import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    public int uniqueId = 0;

    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, SubTask> subtasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateId() {
        uniqueId++;
        return uniqueId;
    }

    public HashMap<Integer, Task> getListOfAllTasks() {
        if (!tasks.isEmpty()) {
            return tasks;
        } else return null;
    }

    public HashMap<Integer, SubTask> getListOfAllSubTasks() {
        if (!subtasks.isEmpty()) {
            return subtasks;
        } else return null;
    }

    public HashMap<Integer, Epic> getListOfAllEpics() {
        if (!epics.isEmpty()) {
            return epics;
        } else return null;
    }

    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    public void removeAllSubTasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
        }
    }

    public Task getTaskById(int id) {
        if (!tasks.isEmpty()) {
            return tasks.get(id);
        } else return null;
    }

    public SubTask getSubTaskById(int id) {
        if (!subtasks.isEmpty()) {
            return subtasks.get(id);
        } else return null;
    }

    public Epic getEpicById(int id) {
        if (!epics.isEmpty()) {
            return epics.get(id);
        } else return null;
    }

    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void addSubTask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
        getEpic(subtask.getEpicId()).addSubtask(subtask.getId());
        setEpicStatus(subtask.getEpicId());
    }

    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
            tasks.put(task.getId(), task);
    }

    public void updateSubTask(SubTask subtask) {
        if (!epics.isEmpty()) {
            subtasks.put(subtask.getId(), subtask);
            setEpicStatus(subtask.getEpicId());
            System.out.println("Подзадача загружена.");
        } else {
            System.out.println("Сначала нужно создать эпик!");
        }
    }

    public void updateEpic(Epic epic) {
            epics.put(epic.getId(), epic);
            setEpicStatus(epic.getId());
    }

    public void removeTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            tasks.remove(taskId);
        }
    }

    public void removeSubTaskById(int subtaskId) {
        if (!subtasks.isEmpty()) {
            getEpic(subtasks.get(subtaskId).getEpicId()).removeSubTaskById(subtaskId);
            subtasks.remove(subtaskId);
        }
    }

    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            if (!subtasks.isEmpty() && !epic.isSubTasksEmpty()) {
                for (int subtaskId : epic.getSubTasksList()) {
                    subtasks.remove(subtaskId);
                }
                epic.clearSubtasksList();
            }
            epics.remove(epicId);
        }
    }

    public ArrayList<Integer> getSubtasksByEpicId(int epicId) {
        if (epics.containsKey(epicId)) {
            return getEpic(epicId).getSubTasksList();
        } else return null;
    }

    private void setEpicStatus(int epicId) {
        int newCounter = 0;
        int inProgressCounter = 0;
        int doneCounter = 0;

        Epic epic = epics.get(epicId);

        if (!epic.isSubTasksEmpty()) {
            for (SubTask subtask : subtasks.values()) {
                if (subtask.getEpicId() == epicId) {
                    switch (subtask.getStatus()) {
                        case "NEW":
                            newCounter += 1;
                            break;
                        case "IN_PROGRESS":
                            inProgressCounter += 1;
                            break;
                        case "DONE":
                            doneCounter += 1;
                            break;
                    }
                }
            }

            if (inProgressCounter == 0 && doneCounter == 0 && newCounter != 0) {
                epic.setStatus("NEW");
            } else if (inProgressCounter == 0 && newCounter == 0 && doneCounter != 0) {
                epic.setStatus("DONE");
            } else {
                epic.setStatus("IN_PROGRESS");
            }
        } else {
            epic.setStatus("NEW");
        }
    }

    private Epic getEpic(int epicId) {
        return epics.get(epicId);
    }
}
