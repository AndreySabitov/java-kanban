package services;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.util.List;

class InMemoryTaskManagerTest {
    @Test
    void tasksWithSameIdIsSame() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(taskManager.getTask(task1Id), taskManager.getTask(task1Id));
    }

    @Test
    void epicsWithSameIdIsSame() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertEquals(taskManager.getEpic(epic1Id), taskManager.getEpic(epic1Id));
    }

    @Test
    void subtasksWithSameIdIsSame() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        assertEquals(taskManager.getSubTask(subtask1Id), taskManager.getSubTask(subtask1Id));
    }

    @Test
    void cantMakeEpicYourOwnSubtask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask = new Subtask(epic1Id, epic1Id, "эпик подзадача",
                "пытаюсь сделать эпик подзадачей", TaskStatuses.NEW);
        Integer subtaskEpicId = taskManager.addNewSubtask(subtask);
        assertNull(subtaskEpicId);
    }

    @Test
    void cantMakeSubtaskYourOwnEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        Subtask subtaskOfSubtask = new Subtask(subtask1Id, "подзадача - Эпик",
                "хочу сделать подзадачу своим эпиком", TaskStatuses.NEW);
        Integer epicSubtaskId = taskManager.addNewSubtask(subtaskOfSubtask);
        assertNull(epicSubtaskId);
    }

    @Test
    void checkCreateTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertNotNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkCreateEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkCreateSubtask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        assertNotNull(taskManager.getSubTask(subtask1Id));
    }

    @Test
    void checkNoConflictBetweenTasksWithGeneratedIdAndWithGivenId() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task(task1Id, "задача 2", "задача 2 с таким же id", TaskStatuses.NEW);
        int task2Id = taskManager.addNewTask(task2);
        assertTrue(task1Id != task2Id);
    }

    @Test
    void checkTaskNotChangeWhenAddedToManager() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(task1, taskManager.getTask(task1Id));
    }

    @Test
    void checkAddTaskToHistory() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void checkMaxSizeOfHistoryIs10() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
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
        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.getEpic(epic1Id);
        taskManager.getEpic(epic2Id);
        taskManager.getEpic(epic3Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.getSubTask(subtask2Id);
        taskManager.getSubTask(subtask3Id);
        taskManager.getTask(task2Id);
        taskManager.getEpic(epic3Id);
        taskManager.getSubTask(subtask2Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size());
    }
}