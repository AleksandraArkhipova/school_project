package manager_and_tasks;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int uniqueId = 0;

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public int generateId() {
        uniqueId++;
        return uniqueId;
    }

    public HashMap<Integer, Task> getListOfAllTasks() {

        return tasks;
    }

    public HashMap<Integer, SubTask> getListOfAllSubTasks() {

        return subtasks;
    }

    public HashMap<Integer, Epic> getListOfAllEpics() {

        return epics;
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
            subtasks.clear();
        }
    }

    public Task getTaskById(int id) {
            return tasks.get(id);
    }

    public SubTask getSubTaskById(int id) {
            return subtasks.get(id);
    }

    public Epic getEpicById(int id) {
            return epics.get(id);
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

        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        if (!subtasks.isEmpty()) {
            epic.removeSubTaskById(subtaskId);
            subtasks.remove(subtaskId);
        }
        setEpicStatus(epic.getId());
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
            return getEpic(epicId).getSubTasksList();
    }

    private void setEpicStatus(int epicId) {

        int newCounter = 0;
        int inProgressCounter = 0;
        int doneCounter = 0;

        Epic epic = epics.get(epicId);

        if (!epic.isSubTasksEmpty()) {
            for (Integer subtaskId : epic.getSubTasksList()) {
                switch (subtasks.get(subtaskId).getStatus()) {
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
