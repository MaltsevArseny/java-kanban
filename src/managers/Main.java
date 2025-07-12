package managers;  // Добавляем пакет, если класс находится в managers

import java.io.File;

import static managers.http.HttpTaskManagerTasksTest.TEST_PORT;

public class Main {
    private static final int TEST_PORT = 0;

    public static void main(String[] args) {
        try {
            // 1. Вариант с InMemoryTaskManager (данные только в памяти)
            // TaskManager manager = Managers.getDefault();

            // 2. Вариант с FileBackedTaskManager (сохранение в файл)
            TaskManager manager = new FileBackedTaskManager(new File("tasks.csv"));

            // Создаем и запускаем HTTP сервер
            HttpTaskServer server = new HttpTaskServer(manager, TEST_PORT);
            server.start();  // Исправляем wait() на start()

            System.out.println("HTTP Task Server запущен на порту 8080");
            System.out.println("Доступные эндофиты:");
            System.out.println("GET /tasks - получение списка задач");
            System.out.println("GET /subtasks - получение списка подзадач");
            System.out.println("GET /epics - получение списка эпиков");
            System.out.println("GET /history - получение истории просмотров");
            System.out.println("GET /prioritized - получение задач по приоритету");

            // Добавляем ожидание завершения работы сервера
            System.out.println("Нажмите Enter для остановки сервера...");
            System.in.read();

            server.stop();
        } catch (Exception e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
            e.printStackTrace();
        }
    }
}