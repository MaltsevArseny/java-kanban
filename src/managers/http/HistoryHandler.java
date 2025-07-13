package managers.http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private final TaskManager manager;

    public HistoryHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendNotFound(exchange);
            return;
        }

        try {
            List<Task> history = manager.getHistory();
            sendSuccess(exchange, convertHistoryToJson(history));
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private String convertHistoryToJson(List<Task> history) {
        StringBuilder json = new StringBuilder("[");
        for (Task task : history) {
            if (json.length() > 1) json.append(",");
            json.append(String.format(
                    "{\"id\":%d,\"type\":\"%s\",\"name\":\"%s\"}",
                    task.getId(),
                    task.getType().name(),
                    escapeJson(task.getName())
            ));
        }
        return json.append("]").toString();
    }

    // Добавляем метод для экранирования JSON-строк
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
}