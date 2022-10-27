package managers;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import exceptions.ManagerSaveException;
import tasks.Epic;
import tasks.SubTask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static managers.Managers.getGson;

public class HTTPTaskManager extends FileBackedTasksManager {

    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HTTPTaskManager(URI uri) {
        super();
        gson = getGson();
        try {
            kvTaskClient = new KVTaskClient(uri);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка подключения к KVServer", e.getCause());
        }
    }

    @Override
    public void save() {

        try {
            kvTaskClient.put("tasks", gson.toJson(tasksMap));
            kvTaskClient.put("epics", gson.toJson(epicsMap));
            kvTaskClient.put("subtasks", gson.toJson(subtasksMap));
            kvTaskClient.put("history", gson.toJson(historyManager.getHistoryList()));

        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при сохранении данных менеджер");
        }

    }

    public void load() {
        try {
            Map<Integer, Task> tasksMap = gson.fromJson(
                    kvTaskClient.load("tasks"),
                    new TypeToken<Map<Integer, Task>>() {
                    }.getType()
            );
            Map<Integer, Epic> epicsMap = gson.fromJson(
                    kvTaskClient.load("epics"),
                    new TypeToken<Map<Integer, Epic>>() {
                    }.getType()
            );
            Map<Integer, SubTask> subtasksMap = gson.fromJson(
                    kvTaskClient.load("subtasks"),
                    new TypeToken<Map<Integer, SubTask>>() {
                    }.getType()
            );
            List<Task> historyList = gson.fromJson(
                    kvTaskClient.load("history"),
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            this.tasksMap = tasksMap;
            this.epicsMap = epicsMap;
            this.subtasksMap = subtasksMap;
            this.setOfPrioritizedTasks.addAll(tasksMap.values());
            this.setOfPrioritizedTasks.addAll(subtasksMap.values());

            HistoryManageable historyManager = new InMemoryHistoryManager();
            historyList.forEach(historyManager::add);
            this.historyManager = historyManager;

        } catch (IOException | InterruptedException e) {
            System.out.println("Ошибка при загрузке данных менеджера");

        }
    }
}
