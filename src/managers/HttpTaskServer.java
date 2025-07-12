package managers;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import managers.http.*;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final HttpServer server;

    public HttpTaskServer(TaskManager manager, int testPort) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Регистрация обработчиков
        server.createContext("/tasks", (HttpHandler) new TasksHandler(manager));
        server.createContext("/subtasks", (HttpHandler) new SubtasksHandler(manager));
        server.createContext("/epics", (HttpHandler) new EpicsHandler(manager));
        server.createContext("/history", (HttpHandler) new HistoryHandler(manager));
        server.createContext("/prioritized", (HttpHandler) new PrioritizedHandler(manager));
    }

    public void start() {
        server.start();
        System.out.println("HTTP Task Server started on port 8080");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP Task Server stopped");
    }
}