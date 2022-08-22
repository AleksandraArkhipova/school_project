package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

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

    void addTask(Task task);

    void addSubTask(SubTask subtask);

    void addEpic(Epic epic);

    void updateTask(Task task);

    void updateSubTask(SubTask subtask);

    void updateEpic(Epic epic);

    void removeTaskById(int taskId);

    void removeSubTaskById(int subtaskId);

    void removeEpicById(int epicId);

    List<Integer> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

}
