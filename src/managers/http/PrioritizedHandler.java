package managers.http;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import tasks.Task;
import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if ("GET".equals(exchange.getRequestMethod())) {
                List<Task> prioritized = taskManager.getPrioritizedTasks();
                String response = convertTasksToJson(prioritized);
                sendSuccess(exchange, response);
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private String convertTasksToJson(List<Task> tasks) {
        StringBuilder json = new StringBuilder("[");
        for (Task task : tasks) {
            if (json.length() > 1) {
                json.append(",");
            }
            json.append(convertTaskToJson(task));
        }
        json.append("]");
        return json.toString();
    }

    private String convertTaskToJson(Task task) {
        return String.format(
                "{\"id\":%d,\"name\":\"%s\",\"status\":\"%s\",\"description\":\"%s\"}",
                task.getId(),
                escapeJson(task.getName()),
                task.getStatus().name(),
                escapeJson(task.getDescription())
        );
    }
}