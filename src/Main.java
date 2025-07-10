import managers.FileBackedTaskManager;
import managers.Managers;
import tasks.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        File file = null;
        try {
            // 1. Создаем временный файл для хранения данных
            file = File.createTempFile("tasks", ".csv");

            // 2. Создаем менеджер с файловым хранилищем
            FileBackedTaskManager manager = Managers.getFileBackedManager(file);

            // 3. Создаем задачи разных типов
            Task task1 = new Task(1, "Помыть посуду", "Помыть всю посуду вечером", TaskStatus.NEW);
            Epic epic1 = new Epic(2, "Переезд", "Организация переезда в новый офис");
            Subtask subtask1 = new Subtask(3, "Упаковать вещи", "Упаковать офисные принадлежности",
                    TaskStatus.NEW, epic1.getId());

            // 4. Добавляем задачи в менеджер
            manager.addNewTask(task1);
            manager.addNewEpic(epic1);
            manager.addNewSubtask(subtask1);

            // 5. Выводим созданные задачи
            System.out.println("=== Все задачи ===");
            System.out.println("Задачи:");
            manager.getTasks().forEach(System.out::println);
            System.out.println("\nЭпики:");
            manager.getEpics().forEach(System.out::println);
            System.out.println("\nПодзадачи:");
            manager.getSubtasks().forEach(System.out::println);

            // 6. Изменяем статусы некоторых задач
            task1.setStatus(TaskStatus.IN_PROGRESS);
            manager.updateTask(task1);

            subtask1.setStatus(TaskStatus.DONE);
            manager.updateSubtask(subtask1);

            // 7. Проверяем обновление статуса эпика
            System.out.println("\n=== После обновления статусов ===");
            System.out.println("Статус эпика: " + manager.getEpic(epic1.getId()).getStatus());

            // 8. Создаем новый менеджер из файла
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

            // 9. Проверяем загруженные данные
            System.out.println("\n=== Данные после загрузки из файла ===");
            System.out.println("Задачи:");
            loadedManager.getTasks().forEach(System.out::println);
            System.out.println("\nЭпики:");
            loadedManager.getEpics().forEach(System.out::println);
            System.out.println("\nПодзадачи:");
            loadedManager.getSubtasks().forEach(System.out::println);

            // 10. Проверяем историю просмотров
            System.out.println("\n=== История просмотров ===");
            loadedManager.getHistory().forEach(System.out::println);

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Произошла ошибка при работе с файлом", e);
            System.err.println("Произошла критическая ошибка. Подробности в логах.");
        } finally {
            if (file != null) {
                file.deleteOnExit();
            }
        }
    }
}