package servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.HTTPTaskManager;
import tasks.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;
import static managers.Managers.*;

public class HttpTaskServer {
    public static final int PORT = 8888;
    private final HttpServer server;
    private final HTTPTaskManager httpTaskManager;
    private final Gson gson;


    public HttpTaskServer(HTTPTaskManager httpTaskManager) throws IOException {
        this.httpTaskManager = httpTaskManager;
        gson = getGson();
        server = HttpServer.create(
                new InetSocketAddress("localhost",
                        PORT),
                0);
        server.createContext("/api/v1/tasks", new TasksHandler());
    }

    public HTTPTaskManager getHttpTaskManager() {
        return httpTaskManager;
    }

    public void start() {
        System.out.println("Started HttpTaskServer on port " + PORT);
        System.out.println("http://localhost:" + PORT + "/api/v1/tasks/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Stopped HttpTaskServer on port " + PORT + "/api/v1/tasks/");
    }

    protected class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange httpExchange) {
            try {
                System.out.println(httpExchange.getRequestURI());
                String requestMethod = httpExchange.getRequestMethod();
                String path = httpExchange.getRequestURI().getPath();

                switch (requestMethod) {
                    case "GET": {

                        if (path.contains("/api/v1/tasks/task/prioritized")) {
                            String response = gson.toJson(httpTaskManager.getSetOfPrioritizedTasks());
                            sendText(httpExchange, response);
                            return;
                        }

                        if (path.contains("/api/v1/tasks/history")) {
                            String response = gson.toJson(httpTaskManager.getHistory());
                            sendText(httpExchange, response);
                            return;
                        }
                        if (path.contains("/api/v1/tasks/task")) {
                            int id = parseIdFromQuery(httpExchange);
                            String response;
                            if (id == -1) {
                                response = gson.toJson(httpTaskManager.getListOfAllTasks());
                            } else {
                                response = gson.toJson(httpTaskManager.getTaskById(id));
                            }
                            sendText(httpExchange, response);
                            return;
                        }

                        if (path.contains("/api/v1/tasks/subtask")) {
                            int id = parseIdFromQuery(httpExchange);
                            String response;
                            if (id == -1) {
                                response = gson.toJson(httpTaskManager.getListOfAllSubTasks());
                            } else {
                                response = gson.toJson(httpTaskManager.getSubTaskById(id));
                            }
                            sendText(httpExchange, response);
                            return;
                        }

                        if (path.contains("/api/v1/tasks/epic")) {
                            int id = parseIdFromQuery(httpExchange);
                            String response;
                            if (id == -1) {
                                response = gson.toJson(httpTaskManager.getListOfAllEpics());
                            } else {
                                response = gson.toJson(httpTaskManager.getEpicById(id));
                            }
                            sendText(httpExchange, response);
                            return;
                        }

                        if (path.contains("/api/v1/tasks/subtask/epic")) {
                            int id = parseIdFromQuery(httpExchange);
                            if (id != -1) {
                                String response = gson.toJson(httpTaskManager.getSubtasksByEpicId(id));
                                sendText(httpExchange, response);
                                return;
                            }
                        }
                        break;
                    }
                    case "DELETE": {
                        if (path.contains("/api/v1/tasks/task")) {
                            int id = parseIdFromQuery(httpExchange);
                            if (id == -1) {
                                httpTaskManager.removeAllTasks();
                                System.out.println("Удалили все таски");
                                httpExchange.sendResponseHeaders(200, 0);
                            } else {
                                httpTaskManager.removeTaskById(id);
                                System.out.println("Удалили таску с id = " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }

                        if (path.contains("/api/v1/tasks/subtask")) {
                            int id = parseIdFromQuery(httpExchange);
                            if (id == -1) {
                                httpTaskManager.removeAllSubTasks();
                                System.out.println("Удалили все сабтаски");
                                httpExchange.sendResponseHeaders(200, 0);
                            } else {
                                httpTaskManager.removeSubTaskById(id);
                                System.out.println("Удалили сабтаску с id = " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }

                        if (path.contains("/api/v1/tasks/epic")) {
                            int id = parseIdFromQuery(httpExchange);
                            if (id == -1) {
                                httpTaskManager.removeAllEpics();
                                System.out.println("Удалили все эпики");
                                httpExchange.sendResponseHeaders(200, 0);
                            } else {
                                httpTaskManager.removeEpicById(id);
                                System.out.println("Удалили эпик с id = " + id);
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }
                        break;
                    }
                    case "POST": {

                        if (path.contains("/api/v1/tasks/task")) {

                            String taskStr = readText(httpExchange);
                            Task task = gson.fromJson(taskStr, Task.class);
                            if (!httpTaskManager.getTasksMap().containsKey(task.getId())) {
                                httpTaskManager.addTask(task);
                                System.out.println("Добавили таску с id = " + task.getId());
                                httpExchange.sendResponseHeaders(201, 0);
                            } else {
                                httpTaskManager.updateTask(task);
                                System.out.println("Обновили таску с id = " + task.getId());
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }

                        if (path.contains("/api/v1/tasks/subtask")) {

                            String subtaskStr = readText(httpExchange);
                            SubTask subtask = gson.fromJson(subtaskStr, SubTask.class);
                            if (!httpTaskManager.getSubtasksMap().containsKey(subtask.getId())) {
                                httpTaskManager.addSubTask(subtask);
                                System.out.println("Добавили сабтаску с id = " + subtask.getId());
                                httpExchange.sendResponseHeaders(201, 0);
                            } else {
                                httpTaskManager.updateSubTask(subtask);
                                System.out.println("Обновили сабтаску с id = " + subtask.getId());
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }

                        if (path.contains("/api/v1/tasks/epic")) {

                            String epicStr = readText(httpExchange);
                            Epic epic = gson.fromJson(epicStr, Epic.class);
                            if (!httpTaskManager.getEpicsMap().containsKey(epic.getId())) {
                                httpTaskManager.addEpic(epic);
                                System.out.println("Добавили эпик с id = " + epic.getId());
                                httpExchange.sendResponseHeaders(201, 0);
                            } else {
                                httpTaskManager.updateEpic(epic);
                                System.out.println("Обновили эпик с id = " + epic.getId());
                                httpExchange.sendResponseHeaders(200, 0);
                            }
                        }
                        break;
                    }
                    default: {
                        System.out.println("Ждем GET, POST или DELETE запрос, а получили - " + requestMethod);
                        httpExchange.sendResponseHeaders(405, 0);
                    }
                }


            } catch (Exception exception) {
                exception.printStackTrace();
            } finally {
                httpExchange.close();
            }
        }
    }

    private int parseIdFromQuery(HttpExchange httpExchange) {
        try {
            return Integer.parseInt(httpExchange
                    .getRequestURI()
                    .getQuery()
                    .replace("id=", ""));
        } catch (NumberFormatException | NullPointerException e) {
            return -1;
        }
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        InputStream inputStream = httpExchange.getRequestBody();
        return new String(inputStream.readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        httpExchange.getResponseBody().write(response);
    }
}
