package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManageable {

    FileBackedTasksManager(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file.getName(),
                StandardCharsets.UTF_8))) {

            String line = "";
            StringBuilder sb = new StringBuilder(line);
            while (br.ready()) {
                sb.append(br.readLine());
                sb.append("\n");
            }
            line = sb.toString();
            if (!line.isEmpty()) {
                recoverDataIntoManager(line);
            }
        }
    }

    @Override
    public void addSubTask(SubTask subtask) {
        super.addSubTask(subtask);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();

    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask) {
        super.updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeTaskById(int taskId) {
        super.removeTaskById(taskId);
        save();
    }

    @Override
    public void removeSubTaskById(int subtaskId) {
        super.removeSubTaskById(subtaskId);
        save();
    }

    @Override
    public void removeEpicById(int epicId) {
        super.removeEpicById(epicId);
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public List<Integer> getSubtasksByEpicId(int epicId) {
        List<Integer> listOfSubtasks = super.getSubtasksByEpicId(epicId);
        save();
        return listOfSubtasks;
    }

    private void save() {

        try (FileWriter fileWriter = new FileWriter("FileBackedTasksManager.csv", true)) {

            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write(tasksToString());
            fileWriter.write("\n");
            if (historyManager.getHistoryList() != null) {
                fileWriter.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения!", e);
        }
    }

    private Map<Integer, Task> tasksFromString(String str) {
        Map<Integer, Task> map = new HashMap<>();

        String[] lines = str.split("\n");
        TaskStatuses taskStatus;
        for (String line : lines) {
            if (line != "") {
                try {
                    Task element;
                    String[] elements = line.split(",");

                    taskStatus = TaskStatuses.valueOf(elements[3]);
                    TaskTypes type = TaskTypes.valueOf(elements[1]);
                    int id = Integer.parseInt(elements[0]);
                    String title = elements[2];
                    String description = elements[4];

                    switch (type) {

                        case TASK:
                            element = new Task(title, description, taskStatus, id);
                            break;
                        case SUBTASK:
                            int epicId = Integer.parseInt(elements[5]);
                            element = new SubTask(title, description, taskStatus, id, epicId);
                            break;
                        case EPIC:
                            element = new Epic(title, description, id);
                            element.setStatus(taskStatus);
                            break;
                        default:
                            continue;
                    }
                    map.put(element.getId(), element);

                } catch (RuntimeException exc) {
                    continue;
                }
            }
        }
        return map;
    }

    private static String historyToString(HistoryManageable manager) {
        List<Task> historyList = manager.getHistoryList();
        StringBuilder sb = new StringBuilder();
        for (Task element : historyList) {
            sb.append(element.getId());
            sb.append(",");
        }
        return sb.toString();
    }

    private String tasksToString() {
        StringBuilder sb = new StringBuilder();
        List<Task> elements = new ArrayList<>(getListOfAllTasks());
        elements.addAll(getListOfAllSubTasks());
        elements.addAll(getListOfAllEpics());
        for (Task element : elements) {
            sb.append(element);
            sb.append("\n");
        }


        return sb.toString();
    }

    private static List<Integer> historyFromString(String str) {
        List<Integer> list = new ArrayList<>();
        String[] lines = str.split("\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                line = line.trim();
                String[] elements = line.split(",");
                String type = elements[1];
                if (!elements[0].equals("id") && !type.equals("TASK")
                        && !type.equals("SUBTASK") && !type.equals("EPIC")) {
                    for (String element : elements) {
                        list.add(Integer.parseInt(element));
                    }
                }
            }
        }
        return list;
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        return new FileBackedTasksManager(file);
    }

    private void recoverDataIntoManager(String str) {

        List<Integer> listOfIds = historyFromString(str);
        Map<Integer, Task> mapOfTasks = tasksFromString(str);

        int maxId = 0;
        for (Task element : mapOfTasks.values()) {
            if (element.getId() > maxId) {
                maxId = element.getId();
            }
            TaskTypes taskType = element.getType();
            switch (taskType) {
                case EPIC:
                    epics.put(element.getId(), (Epic) element);
                    break;
                case TASK:
                    tasks.put(element.getId(), element);
                    break;
                case SUBTASK:
                    subtasks.put(element.getId(), (SubTask) element);
                    break;
            }
        }
        for (SubTask subTask : subtasks.values()) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubtask(subTask.getId());
        }

        uniqueId = maxId;

        for (Integer id : listOfIds) {
            historyManager.add(mapOfTasks.get(id));
        }
    }
}

