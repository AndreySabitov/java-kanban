package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    @Test
    void checkDurationOfEpicIsSumDurationsOfItsSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1", "Описание подзадачи 1",
                TaskStatuses.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 2", "Описание подзадачи 2",
                TaskStatuses.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(35)));
        Epic epic = taskManager.getEpic(epic1Id);
        assertEquals(60, epic.getDuration().toMinutes());
    }

    @Test
    void checkThatDurationIsZeroIfSubtasksOfEpicIsEmpty() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertEquals(0, epic.getDuration().toMinutes());
    }

    @Test
    void checkStartTimeOfEpicIsStartTimeOfEarliestSubtask() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1", "Описание подзадачи 1",
                TaskStatuses.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(60)));
        Epic epic = taskManager.getEpic(epic1Id);
        Subtask subtask = taskManager.getSubTask(subtask1Id);
        assertEquals(epic.getStartTime(), subtask.getStartTime());
    }

    @Test
    void checkStartTimeOfEpicWithoutSubtasksIsNull() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertNull(epic.getStartTime());
    }

    @Test
    void checkEndTimeOfEpicIsEndTimeOfLatestSubtask() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, Duration.ofMinutes(30), LocalDateTime.now()));
        int subtask2Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, Duration.ofMinutes(30),
                LocalDateTime.now().plusMinutes(60)));
        Epic epic = taskManager.getEpic(epic1Id);
        Subtask subtask = taskManager.getSubTask(subtask2Id);
        assertEquals(epic.getEndTime(), subtask.getEndTime());
    }

    @Test
    void checkEndTimeOfEpicWithoutSubtasksIsNull() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertNull(epic.getEndTime());
    }

    @Test
    void checkTaskManagersCanGetListOfPrioritizedTasks() {
        TaskManager taskManager = Managers.getDefault();
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, Duration.ofMinutes(30),
                LocalDateTime.now().minusMinutes(60)));
        List<Task> tasks = List.of(taskManager.getSubTask(subtask1Id), taskManager.getTask(task1Id));
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTaskList();
        assertArrayEquals(prioritizedTasks.toArray(), tasks.toArray());
    }

    @Test
    void checkTasksWithoutStartTimeNotAddToListOfPrioritizedTasks() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), null));
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTaskList();
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void checkSubtasksWithoutStartTimeNotAddToListOfPrioritizedTasks() {
        TaskManager taskManager = Managers.getDefault();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1", "Описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), null));
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTaskList();
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void checkIfTasksOverlapInTimeNewTaskNotAddToManager() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15)));
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().minusMinutes(15)));
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTaskList();
        assertEquals(1, prioritizedTasks.size());
    }
}