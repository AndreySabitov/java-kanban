import services.Managers;
import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatuses;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        myTest(taskManager);
    }

    private static void myTest(TaskManager taskManager) {

        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        int task2Id = taskManager.addNewTask(task2);

        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW);
        Subtask subtask3 = new Subtask(epic1Id, "подзадача 3", "описание подзадачи 3",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int subtask3Id = taskManager.addNewSubtask(subtask3);

        Epic epic2 = new Epic("Эпик 2", "Простой эпик 2");
        int epic2Id = taskManager.addNewEpic(epic2);

        taskManager.getTask(task1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(subtask1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic1Id);
        System.out.println(taskManager.getHistory());
        taskManager.getEpic(epic2Id);
        System.out.println(taskManager.getHistory());
        taskManager.getTask(task2Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(subtask3Id);
        System.out.println(taskManager.getHistory());
        taskManager.getSubTask(subtask2Id);
        System.out.println(taskManager.getHistory());
        taskManager.getTask(task1Id);
        System.out.println(taskManager.getHistory());
        taskManager.deleteTask(task1Id);
        System.out.println(taskManager.getHistory());
        taskManager.deleteEpic(epic1Id);
        System.out.println(taskManager.getHistory());
    }
}

