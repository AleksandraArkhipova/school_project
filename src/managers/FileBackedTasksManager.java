package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManageable {

    FileBackedTasksManager(File file) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(file.getName(), StandardCharsets.UTF_8))) {

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
        updateSubTask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        updateEpic(epic);
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

    private void save() {

        try (FileWriter fileWriter = new FileWriter("FileBackedTasksManager.csv", true)) {
            PrintWriter writer = new PrintWriter("FileBackedTasksManager.csv");
            writer.print("");
            writer.close();
            fileWriter.write("id,type,name,status,description,epic\n");
            fileWriter.write(tasksToString());
            fileWriter.write("\n");
            if (historyManager.getPrintHistoryList() != null) {
                fileWriter.write(historyToString(historyManager));
            }
        } catch (IOException e) {
            try {
                throw new ManagerSaveException("Ошибка сохранения!");
            } catch (ManagerSaveException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private List<Task> tasksFromString(String str) {
        List<Task> list = new ArrayList<>();
        Task element = null;
        String[] lines = str.split("\n");
        for (String line : lines) {
            if (!line.isEmpty()) {
                String[] elements = line.split(",");
                if (!elements[0].isBlank()) {
                    switch (elements[1]) {
                        case "TASK":
                            element = new Task(elements[2], elements[4], null,
                                    Integer.parseInt(elements[0]));

                            break;
                        case "SUBTASK":
                            element = new SubTask(elements[2], elements[4], null,
                                    Integer.parseInt(elements[0]), Integer.parseInt(elements[5]));

                            break;
                        case "EPIC":
                            element = new Epic(elements[2], elements[4],
                                    Integer.parseInt(elements[0]));
                            break;
                        default:
                            continue;
                    }

                    switch (elements[3]) {
                        case "NEW":
                            element.setStatus(TaskStatuses.NEW);
                            break;
                        case "IN_PROGRESS":
                            element.setStatus(TaskStatuses.IN_PROGRESS);
                            break;

                        case "DONE":
                            element.setStatus(TaskStatuses.DONE);
                            break;
                    }
                }
                list.add(element);
            }
        }
        return list;
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
        if (getListOfAllTasks() != null || getListOfAllSubTasks() != null
                || getListOfAllEpics() != null) {
            List<Task> elements = new ArrayList<>(getListOfAllTasks());
            elements.addAll(getListOfAllSubTasks());
            elements.addAll(getListOfAllEpics());

            for (Task element : elements) {
                sb.append(element);
                sb.append("\n");
            }
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
                if (!elements[0].equals("id") && !elements[1].equals("TASK")
                        && !elements[1].equals("SUBTASK") && !elements[1].equals("EPIC")) {
                    for (String element : elements) {
                        try {
                            list.add(Integer.parseInt(element));
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }
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
        List<Task> listOfTasks = tasksFromString(str);
        List<Task> listForHistoryManager = new ArrayList<>();



            for (Task element : listOfTasks) {
                switch (element.toString().substring(2, 3)) {

                    case "E":
                        addEpic((Epic) element);
                        break;
                    case "T":
                        addTask(element);
                        break;
                    case "S":
                        addSubTask((SubTask) element);
                        break;
                }
            }

            if (!listOfIds.isEmpty() && !listOfTasks.isEmpty()) {
                for (Integer id : listOfIds) {
                    for (Task task : listOfTasks) {
                        if (task.getId() == id) {
                            listForHistoryManager.add(task);
                        }
                    }
                }

                historyManager.setPrintHistoryList(listForHistoryManager);
        }
    }

    @Override
    public List<Task> getHistory() {
        save();
        return super.getHistory();
    }
}

