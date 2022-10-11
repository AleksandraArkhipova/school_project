import managers.FileBackedTasksManager;
import managers.TaskStatuses;
import org.junit.jupiter.api.*;
import tasks.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Calendar.JUNE;
import static org.junit.jupiter.api.Assertions.*;

public class BackedTaskManagerTest {

    public FileBackedTasksManager backedTasksManager;

    @BeforeEach
    void beforeEach() throws IOException {
        File file = new File( "test_file.csv");
        FileWriter myWriter = new FileWriter(file);
        myWriter.write("");
        myWriter.write("id,type,name,status,description,duration,starttime,endtime,epic\n" +
                "1,TASK,Забрать посылку с почты,NEW,До 15.06.22,PT1H,2022-05-10T12:00,2022-05-10T13:00\n" +
                "2,TASK,Пропылесосить квартиру,NEW,До 13.06.22,PT1H,2022-05-03T12:00,2022-05-03T13:00\n" +
                "5,SUBTASK,Сделать прививку от бешенства,NEW,Не забыть про штамп!,PT3H,2022-05-22T10:00,2022-05-22T13:00,3\n" +
                "6,SUBTASK,Пройти ветконтроль в аэропорту,DONE,Приехать за 3 часа до вылета,PT3H,2022-05-25T07:00,2022-05-25T10:00,3\n" +
                "7,SUBTASK,Сесть в самолёт,NEW,Переноску можно поставить на колени,PT1H,2022-05-25T10:00,2022-05-25T11:00,3\n" +
                "3,EPIC,Получить полётный сертификат для кота,IN_PROGRESS,Действует 3 дня,PT73H,2022-05-22T10:00,2022-05-25T11:00\n" +
                "4,EPIC,Подготовить велосипед к сезону,NEW,Перед выходными,null,null,null\n" +
                "\n" +
                "2,7,4");
        myWriter.close();
        backedTasksManager = new FileBackedTasksManager(file);
    }

    @DisplayName("GIVEN a new instance of FileBackedTasksManager"
            + "WHEN method .loadFromFile is called"
            + "THEN number")
    @Test
    void test1_loadFromFile() {
        List<Task> listOfBackedTasks = backedTasksManager.getListOfAllTasks();
        assertNotNull(listOfBackedTasks, "Коллекция пустая.");
        assertEquals(2, listOfBackedTasks.size(),
                "Неверное количество элементов коллекции.");

        List<Task> historyList = backedTasksManager.getHistory();
        assertNotNull(listOfBackedTasks, "Коллекция пустая.");
        assertEquals(4, historyList.size(),
                "Неверное количество элементов истории просмотров.");

        backedTasksManager.removeAllTasks();
        List<Task> emptyListOfBackedTasks = backedTasksManager.getListOfAllTasks();
        assertEquals(0, emptyListOfBackedTasks.size(), "Коллекция не пустая.");

        List<Task> historyList2 = backedTasksManager.getHistory();
        assertEquals(2, historyList2.size(),
                "Неверное количество элементов истории просмотров.");
    }

    @DisplayName("GIVEN a new instance of FileBackedTasksManager"
            + "WHEN method .save is called"
            + "THEN number")
    @Test
    void test2_save() {
        backedTasksManager.addTask(new Task("test2_Task",
                "test2_Task_description",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE,
                        3, 12, 0)));

        backedTasksManager.getSubTaskById(5);

        List<Task> listOfBackedTasks = backedTasksManager.getListOfAllTasks();
        assertNotNull(listOfBackedTasks, "Коллекция пустая.");
        assertEquals(3, listOfBackedTasks.size(),
                "Неверное количество элементов коллекции.");

        List<Task> historyList = backedTasksManager.getHistory();
        assertNotNull(historyList, "Коллекция пустая.");
        assertEquals(6, historyList.size(),
                "Неверное количество элементов истории просмотров.");
    }
}
