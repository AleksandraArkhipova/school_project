package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManageable {

    List<Task> getListOfAllTasks();

    List<Task> getListOfAllSubTasks();

    List<Task> getListOfAllEpics();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    Task getTaskById(int id);

    SubTask getSubTaskById(int id);

    Epic getEpicById(int id);

    void addTask(Task task, int id);

    void addSubTask(SubTask subtask, int id);

    void addEpic(Epic epic, int id);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int taskId);

    void removeSubTaskById(int subtaskId);

    void removeEpicById(int epicId);

    ArrayList<Integer> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

}
