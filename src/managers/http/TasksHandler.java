package managers.http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public TasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
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
        List<Task> tasks = manager.getTasks();
        String json = tasksToJson(tasks);
        sendSuccess(exchange, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequest(exchange);
        Task task = parseTask(body);

        try {
            if (task.getId() == 0) {
                manager.addNewTask(task);
                sendCreated(exchange, taskToJson(task));
            } else {
                manager.updateTask(task);
                sendSuccess(exchange, taskToJson(task));
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
            manager.removeAllTasks();
            sendSuccess(exchange, "{\"result\":\"All tasks deleted\"}");
        }
    }

    private String tasksToJson(List<Task> tasks) {
        StringBuilder sb = new StringBuilder("[");
        for (Task task : tasks) {
            if (sb.length() > 1) sb.append(",");
            sb.append(taskToJson(task));
        }
        sb.append("]");
        return sb.toString();
    }

    private String taskToJson(Task task) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"status\":\"%s\",\"description\":\"%s\"}",
                task.getId(),
                escapeJson(task.getName()),
                task.getStatus(),
                escapeJson(task.getDescription())
        );
    }

    private Task parseTask(String json) {
        // Простейший парсинг JSON без Gson
        String idStr = extractValue(json, "id");
        String name = extractValue(json, "name");
        String status = extractValue(json, "status");
        String description = extractValue(json, "description");

        return new Task(
                idStr.isEmpty() ? 0 : Integer.parseInt(idStr),
                name,
                description,
                TaskStatus.valueOf(status),
                Duration.ZERO,
                null
        );
    }

    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":";
        int start = json.indexOf(search) + search.length();
        if (start < search.length()) return "";

        int end = json.indexOf(',', start);
        if (end == -1) end = json.indexOf('}', start);
        if (end == -1) return "";

        String value = json.substring(start, end).trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }
}