import managers.FileBackedTaskManager;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Создаем файл для хранения данных
        File file = new File("tasks.csv");

        // Создаем менеджер задач
        TaskManager manager = new FileBackedTaskManager(file);

        // Создаем и добавляем задачи
        Task task1 = new Task(0, "Помыть посуду", "Помыть всю посуду на кухне",
                TaskStatus.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2023, 1, 1, 10, 0));
        manager.addNewTask(task1);

        Task task2 = new Task(0, "Сделать уборку", "Убраться во всей квартире",
                TaskStatus.NEW, Duration.ofHours(2),
                LocalDateTime.of(2023, 1, 1, 12, 0));
        manager.addNewTask(task2);

        // Создаем эпик с подзадачами
        Epic epic1 = new Epic(0, "Организовать вечеринку", "Подготовка к вечеринке");
        manager.addNewEpic(epic1);
        int epic1Id = epic1.getId();

        Subtask subtask1 = new Subtask(0, "Купить продукты", "Купить еду и напитки",
                TaskStatus.NEW, Duration.ofHours(1),
                LocalDateTime.of(2023, 1, 2, 10, 0), epic1Id);
        manager.addNewSubtask(subtask1);

        Subtask subtask2 = new Subtask(0, "Приготовить еду", "Приготовить блюда для вечеринки",
                TaskStatus.NEW, Duration.ofHours(3),
                LocalDateTime.of(2023, 1, 2, 12, 0), epic1Id);
        manager.addNewSubtask(subtask2);

        // Выводим информацию о задачах
        System.out.println("Все задачи:");
        manager.getTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        manager.getEpics().forEach(System.out::println);

        System.out.println("\nВсе подзадачи:");
        manager.getSubtasks().forEach(System.out::println);

        // Получаем упорядоченный по приоритету список
        System.out.println("\nЗадачи, упорядоченные по приоритету:");
        manager.getPrioritizedTasks().forEach(System.out::println);

        // Пытаемся добавить задачу с пересечением времени (должно вызвать исключение)
        try {
            Task conflictTask = new Task(0, "Конфликтная задача", "Должна вызвать ошибку",
                    TaskStatus.NEW, Duration.ofHours(1),
                    LocalDateTime.of(2023, 1, 1, 11, 0));
            manager.addNewTask(conflictTask);
        } catch (Exception e) {
            System.out.println("\nОшибка при добавлении задачи: " + e.getMessage());
        }

        // Загружаем данные из файла (демонстрация работы FileBackedTaskManager)
        TaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        System.out.println("\nЗагруженные из файла задачи:");
        loadedManager.getTasks().forEach(System.out::println);
    }
}