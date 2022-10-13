package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import tasks.TaskTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class FileBackedTasksManager
        extends InMemoryTaskManager implements TaskManageable {

    private final File file;

    public FileBackedTasksManager(File file) throws IOException {
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
        this.file = file;
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
    public List<Task> getSubtasksByEpicId(int epicId) {
        List<Task> listOfSubtasks = super.getSubtasksByEpicId(epicId);
        save();
        return listOfSubtasks;
    }

    @Override
    public List<Task> getListOfAllTasks() {
        List<Task> list = super.getListOfAllTasks();
        save();
        return list;
    }

    @Override
    public List<Task> getListOfAllSubTasks() {
        List<Task> list = super.getListOfAllSubTasks();
        save();
        return list;
    }

    @Override
    public List<Task> getListOfAllEpics() {
        List<Task> list = super.getListOfAllEpics();
        save();
        return list;
    }

    private void save() {

        try (FileWriter fileWriter = new FileWriter(file, false)) {

            fileWriter.write("id,type,name,status,description,duration,startTime,endTime,epic\n");//печать шапки
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
        Map<Integer, Task> mapOfTasks = new HashMap<>();

        String[] lines = str.split("\n");

        List<String> taskLines = Arrays.stream(lines)
                .filter(line -> !line.isEmpty())
                .filter(line -> line.contains("TASK")
                        || line.contains("SUBTASK")
                        || line.contains("EPIC"))
                .collect(Collectors.toList());

        for (String line : taskLines) {
            String[] parts = line.split(",");

            Task element;
            Duration duration = null;
            LocalDateTime startTime = null;
            LocalDateTime endTime = null;

            int id = Integer.parseInt(parts[0]);
            TaskTypes type = TaskTypes.valueOf(parts[1]);
            String title = parts[2];
            TaskStatuses status = TaskStatuses.valueOf(parts[3]);
            String description = parts[4];
            if (!parts[5].equals("null")) {
                duration = Duration.parse(parts[5]);
                startTime = LocalDateTime.parse(parts[6]);
                endTime = LocalDateTime.parse(parts[7]);
            }

            switch (type) {

                case TASK:
                    element = new Task(title, description, status, duration, startTime);
                    break;

                case SUBTASK:
                    int epicId = Integer.parseInt(parts[8]);
                    element = new SubTask(title, description, status, duration, startTime, epicId);
                    break;

                case EPIC:
                    element = new Epic(title, description);
                    element.setDuration(duration);
                    element.setStartTime(startTime);
                    break;

                default:
                    continue;
            }
            element.setEndTime(endTime);
            element.setId(id);
            mapOfTasks.put(id, element);
        }
        return mapOfTasks;
    }

    private static String historyToString(HistoryManageable manager) {
        List<Task> historyList = manager.getHistoryList();
        return historyList.stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));

    }

    private String tasksToString() {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Task> map = new HashMap<>(tasksMap);
        map.putAll(subtasksMap);
        map.putAll(epicsMap);

        for (Task element : map.values()) {
            sb.append(element).append("\n");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String str) {
        String[] lines = str.split("\n");
        return Arrays.stream(lines)
                .skip(lines.length - 1)
                .filter(s -> !s.isEmpty())
                .flatMap(s -> Arrays.stream(s.split(",")))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    public static FileBackedTasksManager loadFromFile(File file) throws IOException {
        return new FileBackedTasksManager(file);
    }

    private void recoverDataIntoManager(String str) {

        List<Integer> historyList = historyFromString(str);
        Map<Integer, Task> mapOfTasks = tasksFromString(str);

        int maxId = 0;
        for (Task element : mapOfTasks.values()) {
            if (element.getId() > maxId) {
                maxId = element.getId();
            }
            TaskTypes taskType = element.getType();
            switch (taskType) {
                case EPIC:
                    epicsMap.put(element.getId(), (Epic) element);
                    break;
                case TASK:
                    tasksMap.put(element.getId(), element);
                    setOfPrioritizedTasks.add(element);
                    break;
                case SUBTASK:
                    subtasksMap.put(element.getId(), (SubTask) element);
                    setOfPrioritizedTasks.add(element);
                    break;
            }
        }
        for (SubTask subTask : subtasksMap.values()) {
            Epic epic = epicsMap.get(subTask.getEpicId());
            epic.addSubtask(subTask.getId());
        }

        uniqueId = maxId;

        for (Integer id : historyList) {
            historyManager.add(mapOfTasks.get(id));
        }
    }
}

