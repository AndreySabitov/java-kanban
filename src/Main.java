import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatuses;
import tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
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
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        Integer subtask2Id = taskManager.addNewSubtask(subtask2);

        Epic epic2 = new Epic("Эпик 2", "Простой эпик 2");
        int epic2Id = taskManager.addNewEpic(epic2);
        Subtask subtask3 = new Subtask(epic2Id, "подзадача 3", "простая подзадача 3",
                TaskStatuses.NEW);
        Integer subtask3Id = taskManager.addNewSubtask(subtask3);

        int epic3Id = taskManager.addNewEpic(new Epic("Эпик 3", "пустой эпик 3"));

        System.out.println(taskManager.getTasksList());
        System.out.println(taskManager.getEpicsList());
        System.out.println(taskManager.getSubtasksList());

        System.out.println(taskManager.getTask(task1Id));
        System.out.println(taskManager.getSubTask(subtask1Id));
        System.out.println(taskManager.getEpic(epic2Id));

        taskManager.updateTask(new Task(task2Id, "Обновленная задача 2",
                "новое описание задачи 2", TaskStatuses.IN_PROGRESS));
        taskManager.updateSubtask(new Subtask(epic1Id, subtask2Id, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.NEW));
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "обновленная подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.DONE));

        taskManager.updateEpic(new Epic(epic2Id, "Эпик2new", "Новое описание эпика 2"));
        taskManager.updateSubtask(new Subtask(epic2Id, subtask3Id, "обновленная подзадача 3",
                "Описание подзадачи 3", TaskStatuses.DONE));

        System.out.println(taskManager.getEpicSubtasks(epic1Id));

        taskManager.deleteEpic(epic2Id);
        taskManager.deleteTask(task1Id);
        taskManager.deleteSubtask(subtask1Id);
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();
        taskManager.deleteTasks();
    }
}

