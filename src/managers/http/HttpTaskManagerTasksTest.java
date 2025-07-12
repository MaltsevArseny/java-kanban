package managers.http;

import managers.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    public static final int TEST_PORT = 8081;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager, TEST_PORT);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void testAddTask() throws IOException, InterruptedException {
        String taskJson = """
            {
                "name": "Test Task",
                "description": "Test Description",
                "status": "NEW"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Test Task"));
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    void testGetTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Test", "Description", TaskStatus.NEW,
                Duration.ofMinutes(30), LocalDateTime.now());
        manager.addNewTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/tasks?id=1"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Test"));
        assertTrue(response.body().contains("Description"));
    }

    @Test
    void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Original", "Desc", TaskStatus.NEW,
                Duration.ZERO, null);
        manager.addNewTask(task);

        String updatedJson = """
            {
                "id": 1,
                "name": "Updated",
                "description": "New Desc",
                "status": "IN_PROGRESS"
            }
            """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Updated", manager.getTask(1).getName());
        assertEquals(TaskStatus.IN_PROGRESS, manager.getTask(1).getStatus());
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "To Delete", "Desc", TaskStatus.NEW,
                Duration.ZERO, null);
        manager.addNewTask(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/tasks?id=1"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(0, manager.getTasks().size());
    }

    @Test
    void testGetAllTasks() throws IOException, InterruptedException {
        manager.addNewTask(new Task(1, "Task 1", "Desc 1", TaskStatus.NEW,
                Duration.ZERO, null));
        manager.addNewTask(new Task(2, "Task 2", "Desc 2", TaskStatus.DONE,
                Duration.ZERO, null));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + "/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Task 2"));
    }
}