package managers;

import exceptions.TaskTimeValidationException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static managers.TaskStatuses.*;

public class InMemoryTaskManager implements TaskManageable {

    protected int uniqueId = 0;

    protected Map<Integer, Task> tasksMap = new HashMap<>();
    protected Map<Integer, SubTask> subtasksMap = new HashMap<>();
    protected Map<Integer, Epic> epicsMap = new HashMap<>();

    protected final Set<Task> setOfPrioritizedTasks =
            new TreeSet<>(Comparator.nullsLast(Comparator.comparing(Task::getStartTime)));

    protected Map<LocalDateTime, Boolean> timeIntersectionMap =
            new LinkedHashMap<>(15 * 24 * 365);

    protected HistoryManageable historyManager = Managers.getDefaultHistory();

    private int generateId() {
        uniqueId++;
        return uniqueId;
    }

    @Override
    public List<Task> getListOfAllTasks() {
        for (Task task : tasksMap.values()) {
            historyManager.add(task);
        }

        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Task> getListOfAllSubTasks() {

        for (Task subtask : subtasksMap.values()) {
            historyManager.add(subtask);
        }
        return new ArrayList<>(subtasksMap.values());
    }

    public Set<Task> getSetOfPrioritizedTasks() {
        return setOfPrioritizedTasks;
    }

    public Boolean checkIfTaskIntersection(Task element) {

        LocalDateTime timeInterval = element.getStartTime();
        for (int i = 0; i < element.getDuration().toMinutes() / 15; i++) {
            boolean isContainTimeInterval =
                    timeIntersectionMap.containsKey(timeInterval);
            if (isContainTimeInterval) {
                throw new TaskTimeValidationException("An intersection has found");
            }
            timeInterval = timeInterval.plusMinutes(15);
        }
        return false;
    }

    private void putInTimeIntersectionMap(Task element) {

        LocalDateTime dateTimeToCheck = element.getStartTime();
        for (int i = 0; i < element.getDuration().toMinutes() / 15; i++) {
            timeIntersectionMap.put(dateTimeToCheck, false);
            dateTimeToCheck = dateTimeToCheck.plusMinutes(15);
        }
    }

    private void removeFromTimeIntersectionMap(Task element) {
        LocalDateTime dateTimeToCheck = element.getStartTime();
        for (int i = 0; i < element.getDuration().toMinutes() / 15; i++) {
            timeIntersectionMap.remove(dateTimeToCheck);
            dateTimeToCheck = dateTimeToCheck.plusMinutes(15);
        }
    }

    private void changeEpicsDateTimeParametersWhenSubtasksAddedChangedOrDeleted(int epicId) {
        Epic epic = getEpic(epicId);
        boolean anySubtasksInList = !epic.getSubTasksList().isEmpty();
        if (anySubtasksInList) {
            LinkedList<SubTask> list = epic.getSubTasksList().stream()
                    .map(subtasksMap::get)
                    .sorted(Comparator.comparing(Task::getStartTime))
                    .collect(Collectors.toCollection(LinkedList::new));

            LocalDateTime start = list.getFirst().getStartTime();
            LocalDateTime end = list.getLast().getEndTime();

            epic.setStartTime(start);
            epic.setEndTime(end);

            Duration duration = list.stream()
                    .map(Task::getDuration)
                    .reduce(Duration.ZERO, Duration::plus);
            epic.setDuration(duration);
        } else {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
        }
    }

    @Override
    public List<Task> getListOfAllEpics() {
        for (Epic epic : epicsMap.values()) {
            historyManager.add(epic);
        }
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public void removeAllTasks() {
        if (!tasksMap.isEmpty()) {
            for (Task task : tasksMap.values()) {
                historyManager.remove(task.getId());
                setOfPrioritizedTasks.remove(task);
                removeFromTimeIntersectionMap(task);
            }
            tasksMap.clear();

        }
    }

    @Override
    public void removeAllSubTasks() {
        if (!subtasksMap.isEmpty()) {
            for (Task subtask : subtasksMap.values()) {
                historyManager.remove(subtask.getId());
                setOfPrioritizedTasks.remove(subtask);
                removeFromTimeIntersectionMap(subtask);
            }
            subtasksMap.clear();

        }
        for (Epic epic : epicsMap.values()) {
            epic.clearSubtasksList();
            setEpicStatusAfterSubtaskAddedOrUpdated(epic.getId());
        }
    }

    @Override
    public void removeAllEpics() {
        removeAllSubTasks();
        if (!epicsMap.isEmpty()) {
            for (Epic epic : epicsMap.values()) {
                historyManager.remove(epic.getId());
            }
            epicsMap.clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        historyManager.add(tasksMap.get(id));
        return tasksMap.get(id);
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subtasksMap.get(id));
        return subtasksMap.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        historyManager.add(epicsMap.get(id));
        return epicsMap.get(id);
    }

    @Override
    public void addTask(Task task) {

        try {
            checkIfTaskIntersection(task);
            if (task.getId() == 0) {
                int id = generateId();
                task.setId(id);
            }

            if (!tasksMap.containsKey(task.getId())) {
                task.setEndTimeForTaskOrSubtask();
                tasksMap.put(task.getId(), task);
                setOfPrioritizedTasks.add(task);
                putInTimeIntersectionMap(task);
            }
        } catch (TaskTimeValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void addSubTask(SubTask subtask) {
        try {
            checkIfTaskIntersection(subtask);

            if (subtask.getId() == 0) {
                int id = generateId();
                subtask.setId(id);
            }

            int epicId = subtask.getEpicId();
            if (getEpic(epicId) != null && !subtasksMap.containsKey(subtask.getId())) {

                subtask.setEndTimeForTaskOrSubtask();
                subtasksMap.put(subtask.getId(), subtask);
                setOfPrioritizedTasks.add(subtask);
                putInTimeIntersectionMap(subtask);

                Epic epic = getEpic(epicId);
                epic.addSubtask(subtask.getId());

                setEpicStatusAfterSubtaskAddedOrUpdated(epicId);
                changeEpicsDateTimeParametersWhenSubtasksAddedChangedOrDeleted(epicId);

            }

        } catch (TaskTimeValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void addEpic(Epic epic) {
        if (epic.getId() == 0) {
            int id = generateId();
            epic.setId(id);
        }
        int epicId = epic.getId();
        if (!epicsMap.containsKey(epicId)) {
            epicsMap.put(epicId, epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        try {
            int taskId = task.getId();
            if (tasksMap.containsKey(taskId)) {
                removeFromTimeIntersectionMap(tasksMap.remove(taskId));
                checkIfTaskIntersection(task);

                task.setEndTimeForTaskOrSubtask();

                tasksMap.put(taskId, task);

                setOfPrioritizedTasks.remove(tasksMap.get(taskId));
                setOfPrioritizedTasks.add(task);

                putInTimeIntersectionMap(task);
            }
        } catch (TaskTimeValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateSubTask(SubTask subtask) {

        try {
            int subtaskId = subtask.getId();
            int epicId = subtask.getEpicId();

            if (epicsMap.containsKey(epicId) && subtasksMap.containsKey(subtaskId)) {
                removeFromTimeIntersectionMap(subtasksMap.remove(subtaskId));
                checkIfTaskIntersection(subtask);

                subtask.setEndTimeForTaskOrSubtask();

                subtasksMap.put(subtaskId, subtask);

                setOfPrioritizedTasks.remove(subtasksMap.get(subtaskId));
                setOfPrioritizedTasks.add(subtask);

                putInTimeIntersectionMap(subtask);

                setEpicStatusAfterSubtaskAddedOrUpdated(epicId);
                changeEpicsDateTimeParametersWhenSubtasksAddedChangedOrDeleted(epicId);
            }
        } catch (TaskTimeValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int epicId = epic.getId();
        if (epicsMap.containsKey(epicId)) {
            epic.setId(epicId);
            epic.setDuration(epicsMap.get(epicId).getDuration());
            epic.setStartTime(epicsMap.get(epicId).getStartTime());
            epic.setEndTime(epicsMap.get(epicId).getEndTime());
            epic.setStatus(epicsMap.get(epicId).getStatus());

            for (Integer id : epicsMap.get(epicId).getSubTasksList()) {
                epic.addSubtask(id);
            }
            epicsMap.put(epicId, epic);
        }
    }

    @Override
    public void removeTaskById(int taskId) {
        Task task = tasksMap.get(taskId);
        if (tasksMap.containsKey(taskId)) {
            setOfPrioritizedTasks.remove(task);
            removeFromTimeIntersectionMap(task);
            tasksMap.remove(taskId);
            historyManager.remove(taskId);
        }
    }

    @Override
    public void removeSubTaskById(int subtaskId) {
        if (subtasksMap.containsKey(subtaskId)) {
            SubTask subtask = subtasksMap.get(subtaskId);
            Epic epic = getEpic(subtask.getEpicId());

            epic.removeSubTaskById(subtaskId);
            subtasksMap.remove(subtaskId);
            setOfPrioritizedTasks.remove(subtask);
            removeFromTimeIntersectionMap(subtask);
            historyManager.remove(subtaskId);
            setEpicStatusAfterSubtaskAddedOrUpdated(epic.getId());
            changeEpicsDateTimeParametersWhenSubtasksAddedChangedOrDeleted(epic.getId());
        }
    }

    @Override
    public void removeEpicById(int epicId) {
        if (epicsMap.containsKey(epicId)) {
            Epic epic = epicsMap.get(epicId);
            if (epic.isContainSubtasks() && !subtasksMap.isEmpty()) {
                for (int subtaskId : epic.getSubTasksList()) {
                    subtasksMap.remove(subtaskId);
                    setOfPrioritizedTasks.remove(subtasksMap.get(subtaskId));
                    removeFromTimeIntersectionMap(subtasksMap.get(subtaskId));
                    historyManager.remove(subtaskId);
                }
                epic.clearSubtasksList();
            }
            epicsMap.remove(epicId);
            historyManager.remove(epicId);
        }
    }

    @Override
    public List<Task> getSubtasksByEpicId(int epicId) {
        List<Integer> listOfSubtasks = getEpic(epicId).getSubTasksList();
        listOfSubtasks.stream()
                .map(this::getSubTaskById)
                .forEach(historyManager::add);

        return listOfSubtasks.stream()
                .map(this::getSubTaskById)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistoryList();

    }

    private void setEpicStatusAfterSubtaskAddedOrUpdated(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic != null && epic.isContainSubtasks()) {

            epic.setStatus(NEW);
            List<Integer> subtasksList = epic.getSubTasksList();
            long doneCounter =
                    subtasksList.stream().filter(x ->
                            subtasksMap.get(x).getStatus() == DONE).count();

            long progressCounter =
                    subtasksList.stream().filter(x ->
                            subtasksMap.get(x).getStatus() == IN_PROGRESS).count();

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
        return epicsMap.get(epicId);
    }

    public Map<Integer, Task> getTasksMap() {
        return tasksMap;
    }

    public Map<Integer, SubTask> getSubtasksMap() {
        return subtasksMap;
    }

    public Map<Integer, Epic> getEpicsMap() {
        return epicsMap;
    }
}

