package managers.http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Subtask;
import tasks.TaskStatus;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public SubtasksHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET": handleGet(exchange); break;
                case "POST": handlePost(exchange); break;
                case "DELETE": handleDelete(exchange); break;
                default: sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendBadRequest(exchange, e.getMessage());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            if (query.startsWith("id=")) {
                int id = Integer.parseInt(query.substring(3));
                Subtask subtask = manager.getSubtask(id);
                if (subtask != null) {
                    sendSuccess(exchange, convertSubtaskToJson(subtask));
                    return;
                }
            } else if (query.startsWith("epic=")) {
                int epicId = Integer.parseInt(query.substring(5));
                List<Subtask> subtasks = getSubtasksByEpic(epicId);
                sendSuccess(exchange, convertSubtasksToJson(subtasks));
                return;
            }
        }
        sendSuccess(exchange, convertSubtasksToJson(manager.getSubtasks()));
    }

    private List<Subtask> getSubtasksByEpic(int epicId) {
        return manager.getSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .toList();
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Subtask subtask = parseSubtaskFromJson(body);

        try {
            if (subtask.getId() == 0) {
                manager.addNewSubtask(subtask);
                sendCreated(exchange, convertSubtaskToJson(subtask));
            } else {
                manager.updateSubtask(subtask);
                sendSuccess(exchange, convertSubtaskToJson(subtask));
            }
        } catch (IllegalArgumentException e) {
            sendBadRequest(exchange, "Invalid epic ID");
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.removeSubtask(id);
            sendSuccess(exchange, "{\"result\":\"Subtask deleted\"}");
        } else {
            manager.removeAllSubtasks();
            sendSuccess(exchange, "{\"result\":\"All subtasks deleted\"}");
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String convertSubtasksToJson(List<Subtask> subtasks) {
        StringBuilder json = new StringBuilder("[");
        for (Subtask subtask : subtasks) {
            if (json.length() > 1) json.append(",");
            json.append(convertSubtaskToJson(subtask));
        }
        return json.append("]").toString();
    }

    private String convertSubtaskToJson(Subtask subtask) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"status\":\"%s\",\"description\":\"%s\",\"epicId\":%d}",
                subtask.getId(),
                escapeJson(subtask.getName()),
                subtask.getStatus().name(),
                escapeJson(subtask.getDescription()),
                subtask.getEpicId()
        );
    }

    private Subtask parseSubtaskFromJson(String json) {
        return new Subtask(
                extractIntValue(json, "id"),
                extractStringValue(json, "name"),
                extractStringValue(json, "description"),
                TaskStatus.valueOf(extractStringValue(json, "status")),
                Duration.ofMinutes(0),
                null,
                extractIntValue(json, "epicId")
        );
    }

    private int extractIntValue(String json, String key) {
        String value = extractStringValue(json, key);
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private String extractStringValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) {
            pattern = "\"" + key + "\":";
            start = json.indexOf(pattern);
            if (start == -1) return "";
            start += pattern.length();
            int end = json.indexOf(",", start);
            if (end == -1) end = json.indexOf("}", start);
            if (end == -1) return "";
            return json.substring(start, end).trim();
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
}