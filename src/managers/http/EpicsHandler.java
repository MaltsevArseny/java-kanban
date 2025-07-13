package managers.http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Epic;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public EpicsHandler(TaskManager manager) {
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
            sendInternalError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Epic epic = manager.getEpic(id);
            if (epic != null) {
                sendSuccess(exchange, epicToJson(epic));
                return;
            }
            sendNotFound(exchange);
        } else {
            sendSuccess(exchange, epicsToJson(manager.getEpics()));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Epic epic = parseEpic(body);

        if (epic.getId() == 0) {
            manager.addNewEpic(epic);
            sendCreated(exchange, epicToJson(epic));
        } else {
            manager.updateEpic(epic);
            sendSuccess(exchange, epicToJson(epic));
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            manager.removeEpic(id);
            sendSuccess(exchange, "{\"result\":\"Epic deleted\"}");
        } else {
            manager.removeAllEpics();
            sendSuccess(exchange, "{\"result\":\"All epics deleted\"}");
        }
    }

    private String epicsToJson(List<Epic> epics) {
        StringBuilder json = new StringBuilder("[");
        for (Epic epic : epics) {
            if (json.length() > 1) json.append(",");
            json.append(epicToJson(epic));
        }
        return json.append("]").toString();
    }

    private String epicToJson(Epic epic) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"status\":\"%s\",\"description\":\"%s\"}",
                epic.getId(),
                escapeJson(epic.getName()),
                epic.getStatus().name(),
                escapeJson(epic.getDescription())
        );
    }

    // Добавленный метод для экранирования JSON
    private String escapeJson(String input) {
        if (input == null) {
            return "";
        }
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private Epic parseEpic(String json) {
        return new Epic(
                extractIntValue(json),
                extractStringValue(json, "name"),
                extractStringValue(json, "description")
        );
    }

    private int extractIntValue(String json) {
        String value = extractStringValue(json, "id");
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
            return json.substring(start, end).replace("\"", "").trim();
        }
        start += pattern.length();
        int end = json.indexOf("\"", start);
        return end > start ? json.substring(start, end) : "";
    }
}