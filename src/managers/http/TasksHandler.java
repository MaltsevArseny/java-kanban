package managers.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;
    private final Gson gson = new Gson();

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            // Запрос одной задачи по ID
            int id = Integer.parseInt(query.substring(3));
            Task task = manager.getTask(id);
            if (task != null) {
                sendSuccess(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } else {
            // Запрос всех задач
            List<Task> tasks = manager.getTasks();
            sendSuccess(exchange, gson.toJson(tasks));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        try {
            // Проверка на пересечение времени
            if (!manager.isTimeSlotAvailable(task)) {
                sendHasOverlaps(exchange);
                return;
            }

            if (task.getId() == 0) {
                // Создание новой задачи
                manager.addNewTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                // Обновление существующей задачи
                manager.updateTask(task);
                sendSuccess(exchange, gson.toJson(task));
            }
        } catch (Exception e) {
            sendBadRequest(exchange, e.getMessage());
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.removeTask(id);
            sendSuccess(exchange, "{\"result\":\"Task deleted\"}");
        } else {
            sendBadRequest(exchange, "Missing 'id' parameter");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "{\"error\":\"Time slot overlaps with existing task\"}";
        exchange.sendResponseHeaders(409, response.getBytes().length);
        exchange.getResponseBody().write(response.getBytes());
        exchange.close();
    }
}