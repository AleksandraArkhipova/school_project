package managers;

import tasks_and_epics.Epic;
import tasks_and_epics.SubTask;
import tasks_and_epics.Task;
import java.util.ArrayList;
import java.util.HashMap;
import static managers.TaskStatuses.*;

public class InMemoryTaskManager implements TaskManageable {

    private int uniqueId = 0;

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();

    HistoryManageable historyManager = new InMemoryHistoryManager();

    @Override
    public int generateId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public HashMap<Integer, Task> getListOfAllTasks() {
        return tasks;
    }

    @Override
    public HashMap<Integer, SubTask> getListOfAllSubTasks() {
        return subtasks;
    }

    @Override
    public HashMap<Integer, Epic> getListOfAllEpics() {
        return epics;
    }

    @Override
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            tasks.clear();
        }
    }

    @Override
    public void removeAllSubTasks() {
        if (!subtasks.isEmpty()) {
            subtasks.clear();
        }
    }

    @Override
    public void removeAllEpics() {
        if (!epics.isEmpty()) {
            epics.clear();
            subtasks.clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void addSubTask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
        getEpic(subtask.getEpicId()).addSubtask(subtask.getId());
        setEpicStatus(subtask.getEpicId());
    }

    @Override
    public void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        if (!epics.isEmpty()) {
            subtasks.put(subtask.getId(), subtask);
            setEpicStatus(subtask.getEpicId());
            System.out.println("Подзадача загружена.");
        } else {
            System.out.println("Сначала нужно создать эпик!");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        setEpicStatus(epic.getId());
    }

    @Override
    public void removeTaskById(int taskId) {
        if (!tasks.isEmpty()) {
            tasks.remove(taskId);
        }
    }

    @Override
    public void removeSubTaskById(int subtaskId) {

        Epic epic = epics.get(subtasks.get(subtaskId).getEpicId());
        if (!subtasks.isEmpty()) {
            epic.removeSubTaskById(subtaskId);
            subtasks.remove(subtaskId);
        }
        setEpicStatus(epic.getId());
    }

    @Override
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

    @Override
    public ArrayList<Integer> getSubtasksByEpicId(int epicId) {
        return getEpic(epicId).getSubTasksList();
    }

    @Override
    public void setEpicStatus(int epicId) {

        int newCounter = 0;
        int inProgressCounter = 0;
        int doneCounter = 0;

        Epic epic = epics.get(epicId);

        if (!epic.isSubTasksEmpty()) {
            for (Integer subtaskId : epic.getSubTasksList()) {
                switch (subtasks.get(subtaskId).getStatus()) {
                    case NEW:
                        newCounter += 1;
                        break;
                    case IN_PROGRESS:
                        inProgressCounter += 1;
                        break;
                    case DONE:
                        doneCounter += 1;
                        break;
                }
            }
            if (inProgressCounter == 0 && doneCounter == 0 && newCounter != 0) {
                epic.setStatus(NEW);
            } else if (inProgressCounter == 0 && newCounter == 0 && doneCounter != 0) {
                epic.setStatus(DONE);
            } else {
                epic.setStatus(IN_PROGRESS);
            }

        } else {
            epic.setStatus(NEW);
        }
    }

    private Epic getEpic(int epicId) {
        return epics.get(epicId);
    }
}