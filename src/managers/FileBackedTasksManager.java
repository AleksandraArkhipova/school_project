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
    public void updateTask(Task task, int taskId) {
        super.updateTask(task, taskId);
        save();
    }

    @Override
    public void updateSubTask(SubTask subtask, int subtaskId) {
        super.updateSubTask(subtask, subtaskId);
        save();
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        super.updateEpic(epic, epicId);
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

        try (FileWriter fileWriter = new FileWriter("FileBackedTasksManager.csv", true)) {

            PrintWriter writer = new PrintWriter("FileBackedTasksManager.csv");
            writer.print("");
            writer.close();
            /** Вернула PrintWriter, объясняю:
             При вызове метода save() файл стирается и наполняется актуальными данными заново.
             Таким образом его внешний вид приводится к тз, где:
             "актуальные добавленные таски,
             \n
             история, выраженная в идентификаторах"

             Если не стирать наполнение файла, тогда нужно делать отдельные реализации save()
             для вызовов методов добавления\просмотра одной задачи и
             для методов добавления/просмотра нескольких задач.
             При такой реализации появляется сложность с историей просмотров, которая должна отображаться
             в файлике последней.
             Если не стирать наполнение файла и при этом вызывать метод просмотра истории
             несколько раз во время работы программы, внешний вид файлика будет такой:
             "актуальные добавленные таски,
             \n
             история, выраженная в идентификаторах",
             "актуальные добавленные таски,
             \n
             история, выраженная в идентификаторах"
             ...
             **/
            fileWriter.write("id,type,name,status,description,duration,startTime,endTime,epic\n");//печать шапки
            fileWriter.write(tasksToString());//добавление в файл актуальных данных по таскам
            fileWriter.write("\n");
            if (historyManager.getHistoryList() != null) {
                fileWriter.write(historyToString(historyManager));//добавление в файл актуальных данных истории просмотров
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
        StringBuilder sb = new StringBuilder();

        historyList.stream()
                .map(Task::getId)
                .map(x -> x + ",")
                .forEach(sb::append);
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    private String tasksToString() {
        StringBuilder sb = new StringBuilder();

        Map<Integer, Task> map = new HashMap<>(tasks);
        map.putAll(subtasks);
        map.putAll(epics);

        for (Task element : map.values()) {
            sb.append(element + "\n");
        }
        return sb.toString();
    }

    private static List<Integer> historyFromString(String str) {
        List<Integer> list = new ArrayList<>();
        String[] lines = str.split("\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                line = line.trim();
                String[] parts = line.split(",");
                boolean isRequiredLine = Arrays.stream(parts).noneMatch(part -> part.startsWith("T")
                        || part.startsWith("E")
                        || part.startsWith("S")
                        || part.startsWith("id")
                );
                if (isRequiredLine) {
                    Arrays.stream(parts)
                            .mapToInt(part -> Integer.parseInt(part))
                            .forEach(list::add);
                }
            }
        }
        return list;
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
                    epics.put(element.getId(), (Epic) element);
                    break;
                case TASK:
                    tasks.put(element.getId(), element);
                    setOfPrioritizedTasks.add(element);
                    break;
                case SUBTASK:
                    subtasks.put(element.getId(), (SubTask) element);
                    setOfPrioritizedTasks.add(element);
                    break;
            }
        }
        for (SubTask subTask : subtasks.values()) {
            Epic epic = epics.get(subTask.getEpicId());
            epic.addSubtask(subTask.getId());
        }

        uniqueId = maxId;

        for (Integer id : historyList) {
            historyManager.add(mapOfTasks.get(id));
        }
    }
}

