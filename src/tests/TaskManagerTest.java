import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskStatuses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import static java.util.Calendar.JUNE;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    protected InMemoryTaskManager taskManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;
    protected Epic epic1;
    protected Epic epic2;
    protected Epic epic3;
    protected SubTask subtask1;
    protected SubTask subtask2;
    protected SubTask subtask3;
    protected SubTask subtask4;
    protected int tasksSize;
    protected int subtasksSize;
    protected int epicsSize;

    @BeforeEach
    void beforeEach() {

        taskManager = Managers.getDefault();
        task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 10, 12, 0));//id1

        task2 = new Task(
                "Пропылесосить квартиру",
                "До 13.06.22",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 3, 12, 0));//id2

        epic1 = new Epic(
                "Получить полётный сертификат для кота",
                "Действует 3 дня");//id3

        epic2 = new Epic(
                "Подготовить велосипед к сезону",
                "Перед выходными");//id4

        subtask1 = new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.NEW,
                Duration.ofHours(3),
                LocalDateTime.of(2022, JUNE,
                        22, 10, 0), 3);//id5

        subtask2 = new SubTask(
                "Пройти ветконтроль в аэропорту",
                "Приехать за 3 часа до вылета",
                TaskStatuses.DONE,
                Duration.ofHours(2),
                LocalDateTime.of(2022, JUNE,
                        25, 5, 0), 3);//id6

        subtask3 = new SubTask(
                "Сесть в самолёт",
                "Переноску можно поставить на колени",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE,
                        25, 10, 0), 3);//id7


        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addEpic(epic1);
        taskManager.addEpic(epic2);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);

    }

    private void initSize() {
        tasksSize = taskManager.getListOfAllTasks().size();
        subtasksSize = taskManager.getListOfAllSubTasks().size();
        epicsSize = taskManager.getListOfAllEpics().size();
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .getListOfAll... is called"
            + "THEN size changes")
    @Test
    void test1_getListOfAllItems() {
        initSize();

        assertEquals(2, tasksSize, "Неверное количество задач.");
        assertEquals(3, subtasksSize, "Неверное количество подзадач.");
        assertEquals(2, epicsSize, "Неверное количество эпиков.");
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .removeAll... is called"
            + "THEN size changes")
    @Test
    void test2_removeAllItems() {
        taskManager.removeAllTasks();
        taskManager.getListOfAllSubTasks();
        taskManager.removeAllEpics();

        initSize();

        assertEquals(0, tasksSize, "Неверное количество задач.");
        assertEquals(0, subtasksSize, "Неверное количество подзадач.");
        assertEquals(0, epicsSize, "Неверное количество эпиков.");
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .get__ById... is called"
            + "THEN size changes")
    @Test
    void test3_getItemById() {

        assertEquals(task1, taskManager.getTaskById(1),
                "Задачи не совпадают");
        assertEquals(subtask1, taskManager.getSubTaskById(5),
                "Подзадачи не совпадают");
        assertEquals(epic1, taskManager.getEpicById(3),
                "Эпики не совпадают");
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .add__ is called"
            + "THEN number")
    @Test
    void test4_addItem() {
        task3 = new Task("test4_Task",
                "test4_Task_description",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, Month.APRIL,
                        1, 12, 0)); //id8
        taskManager.addTask(task3);

        subtask4 = new SubTask("test4_SubTask",
                "test4_SubTask_description",
                TaskStatuses.NEW,
                Duration.ofHours(2),
                LocalDateTime.of(2022, JUNE,
                        3, 3, 0),
                3); //id9
        taskManager.addSubTask(subtask4);

        epic3 = new Epic(
                "test4_Epic",
                "test4_Epic_description"); //id10
        taskManager.addEpic(epic3);

        assertEquals(task3, taskManager.getTaskById(8), "Задачи не совпадают");
        assertEquals(subtask4, taskManager.getSubTaskById(9), "Подзадачи не совпадают");
        assertEquals(epic3, taskManager.getEpicById(10), "Эпики не совпадают");
        initSize();
        assertEquals(3, tasksSize,
                "Неверное количество задач");
        assertEquals(4, subtasksSize,
                "Неверное количество подзадач");
        assertEquals(3, epicsSize,
                "Неверное количество эпиков");
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .update__ is called"
            + "THEN instances change")
    @Test
    void test5_updateItem() {
        task1 = new Task(
                "Забрать посылку с почты",
                "До 20.06.22",
                TaskStatuses.DONE,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 9, 20, 0));

        epic1 = new Epic(
                "Получить ветеринарный сертификат для кота",
                "До конца августа");

        subtask1 = new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.DONE,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 23, 10, 0),
                3);

        taskManager.updateTask(task1, 1);
        taskManager.updateSubTask(subtask1, 5);
        taskManager.updateEpic(epic1, 3);

        assertEquals(task1, taskManager.getTaskById(1), "Задачи не совпадают.");
        assertEquals(subtask1, taskManager.getSubTaskById(5), "Подзадачи не совпадают");
        assertEquals(epic1, taskManager.getEpicById(3), "Эпики не совпадают");

    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .remove__ById is called"
            + "THEN size changes")
    @Test
    void test6_removeItemById() {

        taskManager.removeTaskById(2);
        taskManager.removeSubTaskById(6);
        taskManager.removeEpicById(4);

        initSize();

        assertEquals(1, tasksSize, "Неверное количество задач.");
        assertEquals(2, subtasksSize, "Неверное количество подзадач.");
        assertEquals(1, epicsSize, "Неверное количество эпиков.");

    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN method .getSubtasksByEpicId is called"
            + "THEN the actual collection of subtasks")
    @Test
    void test7_getSubtasksByEpicId() {
        List<Task> list = taskManager.getSubtasksByEpicId(3);

        assertEquals(3, list.size(),
                "Неверное количество подзадач для эпика с id 3");

        taskManager.removeAllSubTasks();
        assertNotNull(list, "Коллекция не пустая");

    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN methods of InMemoryHistoryManager are called"
            + "THEN history should actualize")
    @Test
    void test8_shouldActualizeHistory() {

        List<Task> historyList = taskManager.getHistory();
        taskManager.removeSubTaskById(7);
        List<Task> historyListAfterRemove = taskManager.getHistory();
        assertEquals(historyList, historyListAfterRemove, "Истории просмотров идентичны.");

        int size = historyList.size();
        taskManager.getTaskById(1);
        taskManager.getTaskById(1);
        historyList = taskManager.getHistory();
        assertEquals(size + 1, historyList.size(), "Элементы дублируются.");

        taskManager.removeAllSubTasks();
        taskManager.removeAllTasks();
        taskManager.removeAllEpics();

        historyList = taskManager.getHistory();
        assertTrue(historyList.isEmpty(), "Элементы истории не удалены.");
    }

    @DisplayName("GIVEN a new instance of TaskManager"
            + "WHEN subtasks added/updated"
            + "THEN epics parameters change")
    @Test
    void test9_shouldChangeEpicParameters() {

        assertSame(taskManager.getEpicById(3).getStatus(), TaskStatuses.IN_PROGRESS,
                "Неправильный статус эпика.");

        taskManager.updateSubTask(new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.DONE,
                Duration.ofHours(3),
                LocalDateTime.of(2022, JUNE,
                        22, 10, 0), 3), 5);
        taskManager.updateSubTask(new SubTask(
                "Сесть в самолёт",
                "Переноску можно поставить на колени",
                TaskStatuses.DONE,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE,
                        25, 10, 0), 3), 7);

        assertSame(taskManager.getEpicById(3).getStatus(), TaskStatuses.DONE,
                "Неправильный статус эпика.");

        taskManager.updateSubTask(new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.NEW,
                Duration.ofHours(3),
                LocalDateTime.of(2022, JUNE,
                        22, 10, 0), 3), 5);
        taskManager.updateSubTask(new SubTask(
                "Пройти ветконтроль в аэропорту",
                "Приехать за 3 часа до вылета",
                TaskStatuses.NEW,
                Duration.ofHours(2),
                LocalDateTime.of(2022, JUNE,
                        25, 5, 0), 3), 6);
        taskManager.updateSubTask(new SubTask(
                "Сесть в самолёт",
                "Переноску можно поставить на колени",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE,
                        25, 10, 0), 3), 7);
        assertSame(taskManager.getEpicById(3).getStatus(), TaskStatuses.NEW,
                "Неправильный статус эпика.");

        taskManager.updateSubTask(new SubTask(
                "Сделать прививку от бешенства",
                "Не забыть про штамп!",
                TaskStatuses.IN_PROGRESS,
                Duration.ofHours(3),
                LocalDateTime.of(2022, JUNE,
                        22, 10, 0), 3), 5);
        assertSame(taskManager.getEpicById(3).getStatus(), TaskStatuses.IN_PROGRESS,
                "Неправильный статус эпика.");

        assertEquals(LocalDateTime.of(2022, JUNE,
                        22, 10, 0), taskManager.getEpicById(3).getStartTime(),
                "Неправильно рассчитан startTime.");

        assertEquals(LocalDateTime.of(2022, JUNE,
                        25, 11, 0), taskManager.getEpicById(3).getEndTime(),
                "Неправильно рассчитан endTime");

        assertEquals(Duration.ofSeconds(14400), taskManager.getEpicById(3).getDuration(),
                "Неправильно рассчитан duration");

        taskManager.removeAllSubTasks();
        assertSame(taskManager.getEpicById(3).getStatus(), TaskStatuses.NEW,
                "Неправильный статус эпика.");
    }
}
