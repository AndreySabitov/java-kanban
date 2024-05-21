package services;

import tasks.Epic;
import tasks.SubTask;
import tasks.TaskStatuses;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        taskManager.createTask(new Task("Задача 1", "простая задача 1", taskManager.setID(),
                TaskStatuses.NEW));
        taskManager.createTask(new Task("Задача 2", "простая задача 2", taskManager.setID(),
                TaskStatuses.NEW));

        taskManager.createEpic(new Epic("Эпик 1", "сложный эпик 1", taskManager.setID()));
        taskManager.createSubtask(new SubTask("подзадача 1", "вспомогательная подзадача 1",
                taskManager.setID(), TaskStatuses.NEW, 2));
        taskManager.createSubtask(new SubTask("подзадача 2", "вспомогательная подзадача 2",
                taskManager.setID(), TaskStatuses.NEW, 2));

        taskManager.createEpic(new Epic("Эпик 2", "попроще эпик 2", taskManager.setID()));
        taskManager.createSubtask(new SubTask("подзадача 1", "единственная подзадача 1",
                taskManager.setID(), TaskStatuses.NEW, 5));

        System.out.println(taskManager.getAllTasks());

        taskManager.updateTask(new Task("Задача1", "простая задача 1", 0,
                TaskStatuses.IN_PROGRESS));
        taskManager.updateTask(new Task("Задача2", "простая задача 2", 1,
                TaskStatuses.DONE));

        taskManager.updateEpic(new Epic("Эпик 1", "весьма сложный эпик 1", 2));
        taskManager.updateSubtask(new SubTask("подзадача 1", "вспомогательная подзадача 1",
                3, TaskStatuses.IN_PROGRESS, 2));

        taskManager.updateSubtask(new SubTask("подзадача 1", "единственная подзадача 1", 6,
                TaskStatuses.DONE, 5));

        System.out.println(taskManager.getAllTasks());

        taskManager.deleteTaskById(1);
        taskManager.deleteTaskById(2);

        System.out.println(taskManager.getAllTasks());
    }
}
