package managers;

import tasks_and_epics.Epic;
import tasks_and_epics.SubTask;
import tasks_and_epics.Task;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface TaskManageable {

    int generateId();

    HashMap<Integer, Task> getListOfAllTasks();

    HashMap<Integer, SubTask> getListOfAllSubTasks();

    HashMap<Integer, Epic> getListOfAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void addTask(Task task);

    void addSubTask(SubTask subtask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int taskId);

    void removeSubTaskById(int subtaskId);

    void removeEpicById(int epicId);

    ArrayList<Integer> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

}
