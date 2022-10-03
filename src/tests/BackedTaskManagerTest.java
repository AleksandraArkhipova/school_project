import managers.FileBackedTasksManager;
import managers.TaskStatuses;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tasks.Task;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static java.util.Calendar.JUNE;
import static org.junit.jupiter.api.Assertions.*;


public class BackedTaskManagerTest {

    FileBackedTasksManager backedTasksManager;
    File file = new File(
            "files", "FileBackedTasksManager.csv");

    @BeforeEach
    void beforeEach() {
        try {
            backedTasksManager =
                    FileBackedTasksManager.loadFromFile(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("GIVEN a new instance of FileBackedTasksManager"
            + "WHEN method .loadFromFile is called"
            + "THEN number")
    @Test
    void test1_loadFromFile() throws IOException {
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

        backedTasksManager = FileBackedTasksManager.loadFromFile(file);
        List<Task> historyList2 = backedTasksManager.getHistory();
        assertEquals(2, historyList2.size(),
                "Неверное количество элементов истории просмотров.");
    }

    @DisplayName("GIVEN a new instance of FileBackedTasksManager"
            + "WHEN method .save is called"
            + "THEN number")
    @Test
    void test2_save() throws IOException {
        backedTasksManager.addTask(new Task("test2_Task",
                "test2_Task_description",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE,
                        3, 12, 0)));

        backedTasksManager.getSubTaskById(5);

        backedTasksManager = FileBackedTasksManager.loadFromFile(file);

        List<Task> listOfBackedTasks = backedTasksManager.getListOfAllTasks();
        assertNotNull(listOfBackedTasks, "Коллекция пустая.");
        assertEquals(1, listOfBackedTasks.size(),
                "Неверное количество элементов коллекции.");

        List<Task> historyList = backedTasksManager.getHistory();
        assertNotNull(listOfBackedTasks, "Коллекция пустая.");
        assertEquals(4, historyList.size(),
                "Неверное количество элементов истории просмотров.");
    }

}
