package managers;

import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static managers.TaskStatuses.*;

public class InMemoryTaskManager implements TaskManageable {

    protected int uniqueId = 0;

    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, SubTask> subtasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();

    protected final Set<Task> setOfPrioritizedTasks = new TreeSet<>(
            Comparator.comparing(o -> o.startTime));
    Map<LocalDateTime, Boolean> timeIntersectionMap = new LinkedHashMap<>(15 * 24 * 365);

    protected final HistoryManageable historyManager = Managers.getDefaultHistory();

    private int generateId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public List<Task> getListOfAllTasks() {
        for (Task task : tasks.values()) {
            historyManager.add(task);
        }

        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Task> getListOfAllSubTasks() {

        for (Task subtask : subtasks.values()) {
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtasks.values());
    }

    public Set<Task> getPrioritizedTasksAnsSubtasks() {
        return setOfPrioritizedTasks;
    }

    public Boolean checkIfTaskIntersection(Task task) {
        Set<Task> tasks = getPrioritizedTasksAnsSubtasks();
        boolean isTakenInterSpace = false;
        timeIntersectionMap.clear();
        tasks.forEach(o -> {
            for (int i = 0; i < o.getDuration().toMinutes() / 15; i++) {
                timeIntersectionMap.put(o.startTime, false);
                o.startTime = o.startTime.plusMinutes(15);
            }
        });

        for (int i = 0; i < task.getDuration().toMinutes() / 15; i++) {
            isTakenInterSpace = timeIntersectionMap.containsKey(task.startTime);
            if (!isTakenInterSpace) {
                task.startTime = task.startTime.plusMinutes(15);
            } else {
                break;
            }
        }
        return isTakenInterSpace;
    }

    @Override
    public List<Task> getListOfAllEpics() {
        for (Epic epic : epics.values()) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public void removeAllTasks() {
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                historyManager.remove(task.getId());
                setOfPrioritizedTasks.remove(task);
            }
            tasks.clear();

        }
    }

    @Override
    public void removeAllSubTasks() {
        if (!subtasks.isEmpty()) {
            for (Task subtask : subtasks.values()) {
                historyManager.remove(subtask.getId());
                setOfPrioritizedTasks.remove(subtask);
            }
            subtasks.clear();

        }
        for (Epic epic : epics.values()) {
            epic.clearSubtasksList();
            setEpicStatusAfterSubtaskAddedOrUpdated(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        removeAllSubTasks();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                historyManager.remove(epic.getId());
            }
            epics.clear();
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
        if (!checkIfTaskIntersection(task)) {
            if (task.getId() == 0) {
                int id = generateId();
                task.setId(id);
            }

            if (!tasks.containsKey(task.getId())) {
                task.setEndTime(task.calculateEndTimeForTaskOrSubtask());
                tasks.put(task.getId(), task);
                setOfPrioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void addSubTask(SubTask subtask) {
        if (!checkIfTaskIntersection(subtask)) {
            if (subtask.getId() == 0) {
                int id = generateId();
                subtask.setId(id);
            }

            int epicId = subtask.getEpicId();
            if (getEpic(epicId) != null && !subtasks.containsKey(subtask.getId())) {

                subtask.setEndTime(subtask.calculateEndTimeForTaskOrSubtask());
                subtasks.put(subtask.getId(), subtask);
                setOfPrioritizedTasks.add(subtask);

                Epic epic = getEpic(epicId);
                epic.addSubtask(subtask.getId());

                setEpicStatusAfterSubtaskAddedOrUpdated(epicId);
                setEpicStartTimeIfSubtaskPresent(epicId);
                setEpicEndTimeIfSubTaskPresent(epicId);
                setEpicDurationIfSubTaskPresent(epicId);

            }
        } else {
            System.out.println("Time intersection!");
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            int id = generateId();
            epic.setId(id);
        }
        int epicId = epic.getId();
        if (!epics.containsKey(epicId)) {
            epics.put(epicId, epic);
        }
    }

    @Override
    public void updateTask(Task task, int taskId) {
        if (!checkIfTaskIntersection(task)) {
            if (tasks.containsKey(taskId)) {
                task.setId(taskId);
                task.setEndTime(task.calculateEndTimeForTaskOrSubtask());
                tasks.put(taskId, task);
                setOfPrioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateSubTask(SubTask subtask, int subtaskId) {

        if (!checkIfTaskIntersection(subtask)) {
            int epicId = subtask.getEpicId();

            if (epics.containsKey(epicId)) {
                subtask.setId(subtaskId);
                subtask.setEndTime(subtask.calculateEndTimeForTaskOrSubtask());
                subtasks.put(subtaskId, subtask);
                setOfPrioritizedTasks.add(subtask);

                setEpicStatusAfterSubtaskAddedOrUpdated(epicId);
                setEpicStartTimeIfSubtaskPresent(epicId);
                setEpicEndTimeIfSubTaskPresent(epicId);
                setEpicDurationIfSubTaskPresent(epicId);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic, int epicId) {
        if (epics.containsKey(epicId)) {
            epic.setId(epicId);
            epic.setDuration(epics.get(epicId).getDuration());
            epic.setStartTime(epics.get(epicId).getStartTime());
            epic.setEndTime(epics.get(epicId).getEndTime());
            epic.setStatus(epics.get(epicId).getStatus());

            for (Integer id : epics.get(epicId).getSubTasksList()) {
                epic.addSubtask(id);
            }
            epics.put(epicId, epic);
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        if (tasks.containsKey(taskId)) {
            setOfPrioritizedTasks.remove((tasks.get(taskId)));
            tasks.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void removeSubTaskById(int subtaskId) {
        if (subtasks.containsKey(subtaskId)) {
            SubTask subtask = subtasks.get(subtaskId);
            Epic epic = epics.get(subtask.getEpicId());

            epic.removeSubTaskById(subtaskId);
            subtasks.remove(subtaskId);
            setOfPrioritizedTasks.remove(subtask);

            historyManager.remove(subtaskId);
            setEpicStatusAfterSubtaskAddedOrUpdated(epic.getId());
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epics.containsKey(epicId)) {
            Epic epic = epics.get(epicId);
            if (epic.isContainSubtasks() && !subtasks.isEmpty()) {
                for (int subtaskId : epic.getSubTasksList()) {
                    subtasks.remove(subtaskId);
                    setOfPrioritizedTasks.remove(subtasks.get(subtaskId));
                    historyManager.remove(subtaskId);
                }
                epic.clearSubtasksList();
                setEpicStatusAfterSubtaskAddedOrUpdated(epicId);
            }
            epics.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public List<Integer> getSubtasksByEpicId(int epicId) {
        List<Integer> listOfSubtasks = getEpic(epicId).getSubTasksList();
        for (Integer id : listOfSubtasks) {
            historyManager.add(subtasks.get(id));
        }
        return listOfSubtasks;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistoryList();

    }

    public void setEpicStatusAfterSubtaskAddedOrUpdated(int epicId) {

        Epic epic = getEpic(epicId);
        if (epic != null && epic.isContainSubtasks()) {

            epic.setStatus(NEW);
            List<Integer> subtasksList = epic.getSubTasksList();
            long doneCounter =
                    subtasksList.stream().filter(x ->
                            subtasks.get(x).getStatus() == DONE).count();

            long progressCounter =
                    subtasksList.stream().filter(x ->
                            subtasks.get(x).getStatus() == IN_PROGRESS).count();

            boolean isDone = doneCounter == subtasksList.size();
            if ((doneCounter != 0 || progressCounter != 0) && !isDone) {
                epic.setStatus(IN_PROGRESS);
            }
            if (isDone) {
                epic.setStatus(DONE);
            }
        }
        if (epic != null && !epic.isContainSubtasks()) {
            epic.setStatus(NEW);
        }
    }

    private Epic getEpic(int epicId) {
        return epics.get(epicId);
    }

    protected void setEpicDurationIfSubTaskPresent(int epicId) {
        Epic epic = getEpic(epicId);

        if (epic != null) {
            if (epic.getStartTime() != null && epic.getEndTime() != null) {
                Duration subtasksSumDuration = Duration.between(epic.getStartTime(), epic.getEndTime());
                epic.setDuration(subtasksSumDuration);
            }
        }
    }

    protected void setEpicEndTimeIfSubTaskPresent(int epicId) {
        Epic epic = getEpic(epicId);
        List<Integer> subtaskList = epic.getSubTasksList();
        SubTask lastSubTask = subtasks.get(subtaskList.get(subtaskList.size() - 1));
        LocalDateTime latestSubTaskEndTime = lastSubTask.getEndTime();

        for (Integer id : epic.getSubTasksList()) {
            SubTask subTask = subtasks.get(id);
            if (subTask.getEndTime().isAfter(latestSubTaskEndTime)) {
                latestSubTaskEndTime = subTask.getEndTime();
            }
        }
        epic.setEndTime(latestSubTaskEndTime);
    }

    protected void setEpicStartTimeIfSubtaskPresent(int epicId) {

        Epic epic = getEpic(epicId);
        List<Integer> subtaskList = epic.getSubTasksList();
        SubTask firstSubTask = subtasks.get(subtaskList.get(0));

        LocalDateTime earliestSubTaskStartTime =
                firstSubTask.getStartTime();

        for (Integer id : subtaskList) {
            SubTask subTask = subtasks.get(id);
            if (subTask.getStartTime().isBefore(earliestSubTaskStartTime)) {
                earliestSubTaskStartTime = subTask.getStartTime();
            }
        }
        epic.setStartTime(earliestSubTaskStartTime);
    }
}

