import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import managers.HTTPTaskManager;
import managers.Managers;
import managers.TaskStatuses;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static java.net.http.HttpClient.Version.HTTP_1_1;
import static java.net.http.HttpClient.newBuilder;
import static java.util.Calendar.JUNE;
import static managers.Managers.getGson;
import static org.junit.jupiter.api.Assertions.*;

class HTTPTaskManagerTest {

    URI uri;
    HttpTaskServer httpTaskServer;
    KVServer kvServer;
    Gson gson;
    HTTPTaskManager httpTaskManager;

    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        uri = URI.create("http://localhost:8088");
        gson = getGson();
        httpTaskServer = new HttpTaskServer(Managers.getDefault(uri));
        httpTaskServer.start();
        httpTaskManager = Managers.getDefault(uri);
    }

    @AfterEach
    void stopServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @Test
    void saveAndGetAllTasks_1() {

        HttpClient client = newBuilder()
                .version(HTTP_1_1)
                .build();
        HttpRequest requestForTasks = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task"))
                .version(HTTP_1_1)
                .header("Accept", "application/json")
                .GET()
                .build();
        try {
            HttpResponse<String> response1 = client.send(
                    requestForTasks, HttpResponse.BodyHandlers.ofString());

            assertEquals(200, response1.statusCode());

            Type type = new TypeToken<List<Task>>() {
            }.getType();
            List<Task> tasks = gson.fromJson(response1.body(), type);
            assertNotNull(tasks, "Таски не возвращаются");
        } catch (IOException | InterruptedException e) {
            fail();
        }

    }

    @Test
    void saveAndGetOneTask_2() {

        HttpClient client = newBuilder()
                .version(HTTP_1_1)
                .build();
        Task task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 10, 12, 0));//id1

        HttpRequest requestToAddTask = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        try {
            HttpResponse<String> response2 = client.send(
                    requestToAddTask, HttpResponse.BodyHandlers.ofString());
            assertEquals(201, response2.statusCode());

            task1.setId(1);
            task1.setEndTime(task1.getStartTime().plusHours(1));
            assertEquals(task1, httpTaskServer.getHttpTaskManager().getTaskById(1));
        } catch (IOException | InterruptedException e) {
            fail();
        }
    }

    @Test
    void getOneTaskById_3() throws IOException, InterruptedException {
        HttpClient client = newBuilder()
                .version(HTTP_1_1)
                .build();
        Task task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 10, 12, 0));//id1

        task1.setEndTime(task1.getStartTime().plusHours(1));
        task1.setId(1);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task/?id=1"))
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request1, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            String json = response.body();
            Task task = gson.fromJson(json, Task.class);
            assertEquals(task1, task);
        } catch (IOException | InterruptedException e) {
            fail();
        }
    }

    @Test
    void deleteTaskByIdTest_4() throws IOException, InterruptedException {

        HttpClient client = newBuilder()
                .version(HTTP_1_1)
                .build();
        Task task1 = new Task(
                "Забрать посылку с почты",
                "До 15.06.22",
                TaskStatuses.NEW,
                Duration.ofHours(1),
                LocalDateTime.of(2022, JUNE, 10, 12, 0));//id1

        task1.setEndTime(task1.getStartTime().plusHours(1));

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task/?id=1"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8888/api/v1/tasks/task/?id=1"))
                .DELETE()
                .build();
        try {
            HttpResponse<String> response = client.send(request2, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertNull(httpTaskServer.getHttpTaskManager().getTaskById(1));
        } catch (IOException | InterruptedException e) {
            fail();
        }
    }
}