package managers.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected void sendResponse(HttpExchange exchange, String response, int statusCode) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }

    protected void sendJson(HttpExchange exchange, String json, int statusCode) throws IOException {
        sendResponse(exchange, json, statusCode);
    }

    protected void sendSuccess(HttpExchange exchange, String json) throws IOException {
        sendJson(exchange, json, 200);
    }

    protected void sendCreated(HttpExchange exchange, String json) throws IOException {
        sendJson(exchange, json, 201);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendJson(exchange, "{\"error\":\"Not found\"}", 404);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        sendJson(exchange, "{\"error\":\"" + escapeJson(message) + "\"}", 400);
    }

    protected void sendConflict(HttpExchange exchange) throws IOException {
        sendJson(exchange, "{\"error\":\"Time conflict\"}", 409);
    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        sendJson(exchange, "{\"error\":\"Internal server error\"}", 500);
    }

    protected String readRequest(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }

    String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public abstract void handle(HttpExchange exchange) throws IOException;
}