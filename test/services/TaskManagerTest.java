package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @Test
    void managerCanGetListsOfAllTasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time.plusMinutes(35));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        List<Task> tasks = taskManager.getTasksList();
        assertFalse(tasks.isEmpty());
        List<Epic> epics = taskManager.getEpicsList();
        assertFalse(epics.isEmpty());
        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void checkManagerCanGetEpicSubtasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW, duration, time.plusMinutes(60));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic1Id);
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void cantGetSubtasksOfNonExistentEpic() {
        List<Subtask> subtasks = taskManager.getEpicSubtasks(1);
        assertNull(subtasks);
    }

    @Test
    void returnMinusOneIfTryAddSubtaskToNonExistentEpic() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Subtask subtask1 = new Subtask(1, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertEquals(-1, subtask1Id);
    }

    @Test
    void cantGetNonExistentTask() {
        assertNull(taskManager.getTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentSubtask() {
        assertNull(taskManager.getSubTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentEpic() {
        assertNull(taskManager.getEpic(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantUpdateNonExistentTask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        taskManager.updateTask(new Task(1, "Обновленная задача 2",
                "новое описание задачи 2", TaskStatuses.IN_PROGRESS, duration, time));
        assertNull(taskManager.getTask(1));
    }

    @Test
    void cantUpdateNonExistentEpic() {
        taskManager.updateEpic(new Epic(1, "Эпик2new", "Новое описание эпика 2"));
        assertNull(taskManager.getEpic(1));
    }

    @Test
    void cantUpdateNonExistentSubtask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        taskManager.updateSubtask(new Subtask(epic1Id, 3, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.NEW, duration, time));
        assertNull(taskManager.getSubTask(3));
    }

    @Test
    void checkManagerCanUpdateTask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.updateTask(new Task(task1Id, "Задача 1",
                "Новое описание задачи 1", TaskStatuses.NEW, duration, time.plus(duration)));
        Task task = taskManager.getTask(task1Id);
        assertNotEquals(task, task1);
    }

    @Test
    void checkManagerCanUpdateEpic() {
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        String oldName = epic1.getTaskName();
        String oldDescription = epic1.getTaskDescription();
        int epic1Id = taskManager.addNewEpic(epic1);
        taskManager.updateEpic(new Epic(epic1Id, "Эпик 1", "Новое описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertTrue(!oldName.equals(epic.getTaskName()) || !oldDescription.equals(epic.getTaskDescription()));
    }

    @Test
    void checkManagerCanUpdateSubtask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.NEW, duration, time.plus(duration)));
        Subtask subtask = taskManager.getSubTask(subtask1Id);
        assertNotEquals(subtask, subtask1);
    }

    @Test
    void tasksWithSameIdIsSame() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(taskManager.getTask(task1Id), taskManager.getTask(task1Id));
        assertEquals(taskManager.getTask(task1Id).getDuration(), taskManager.getTask(task1Id).getDuration());
        assertEquals(taskManager.getTask(task1Id).getStartTime(), taskManager.getTask(task1Id).getStartTime());
    }

    @Test
    void epicsWithSameIdIsSame() {
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertEquals(taskManager.getEpic(epic1Id), taskManager.getEpic(epic1Id));
        assertArrayEquals(taskManager.getEpic(epic1Id).getSubtaskIds().toArray(),
                taskManager.getEpic(epic1Id).getSubtaskIds().toArray());
        assertEquals(taskManager.getEpic(epic1Id).getDuration(), taskManager.getEpic(epic1Id).getDuration());
        assertEquals(taskManager.getEpic(epic1Id).getStartTime(), taskManager.getEpic(epic1Id).getStartTime());
        assertEquals(taskManager.getEpic(epic1Id).getEndTime(), taskManager.getEpic(epic1Id).getEndTime());
    }

    @Test
    void subtasksWithSameIdIsSame() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertEquals(taskManager.getSubTask(subtask1Id), taskManager.getSubTask(subtask1Id));
        assertEquals(taskManager.getSubTask(subtask1Id).getIdOfEpic(),
                taskManager.getSubTask(subtask1Id).getIdOfEpic());
        assertEquals(taskManager.getSubTask(subtask1Id).getDuration(),
                taskManager.getSubTask(subtask1Id).getDuration());
        assertEquals(taskManager.getSubTask(subtask1Id).getStartTime(),
                taskManager.getSubTask(subtask1Id).getStartTime());
    }

    @Test
    void cantMakeEpicYourOwnSubtask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask = new Subtask(epic1Id, epic1Id, "эпик подзадача",
                "пытаюсь сделать эпик подзадачей", TaskStatuses.NEW, duration, time);
        int subtaskEpicId = taskManager.addNewSubtask(subtask);
        assertEquals(-1, subtaskEpicId);
    }

    @Test
    void cantMakeSubtaskYourOwnEpic() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Subtask subtaskOfSubtask = new Subtask(subtask1Id, "подзадача - Эпик",
                "хочу сделать подзадачу своим эпиком", TaskStatuses.NEW, duration, time.plus(duration));
        int epicSubtaskId = taskManager.addNewSubtask(subtaskOfSubtask);
        assertEquals(-1, epicSubtaskId);
    }

    @Test
    void checkManagerCanCreateTask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        assertNotNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanDeleteTask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.deleteTask(task1Id);
        assertNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanCreateEpic() {
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanDeleteEpicAndItsSubtasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpic(epic1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanCreateSubtask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time.plus(duration));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertNotNull(taskManager.getSubTask(subtask1Id));
    }

    @Test
    void checkManagerCanDeleteSubtaskAndItsIdDeleteFromItsEpic() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteSubtask(subtask1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertTrue(taskManager.getEpic(epic1Id).getSubtaskIds().isEmpty());
    }

    @Test
    void CheckManagerCanDeleteAllTasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task("Задача 2", "Простая задача 2", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task2Id = taskManager.addNewTask(task2);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasksList().isEmpty());
    }

    @Test
    void checkManagerCanDeleteAllEpics() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpicsList().isEmpty());
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void checkManagerCanDeleteAllSubtasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void checkNoConflictBetweenTasksWithGeneratedIdAndWithGivenId() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task(task1Id, "задача 2", "задача 2 с таким же id", TaskStatuses.NEW,
                duration, time.plus(duration));
        int task2Id = taskManager.addNewTask(task2);
        assertTrue(task1Id != task2Id);
    }

    @Test
    void checkTaskNotChangeWhenAddedToManager() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(task1, taskManager.getTask(task1Id));
        assertEquals(task1.getDuration(), taskManager.getTask(task1Id).getDuration());
        assertEquals(task1.getStartTime(), taskManager.getTask(task1Id).getStartTime());
    }

    @Test
    void checkHistoryManagerSavePreviousVersionOfTask() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.updateTask(new Task(task1Id, "Обновленная задача 1",
                "новое описание задачи 1", TaskStatuses.IN_PROGRESS, duration, time.plus(duration)));
        List<Task> history = taskManager.getHistory();
        Task task2 = history.get(0);
        assertEquals(task1, task2);
        assertEquals(task1.getDuration(), task2.getDuration());
        assertEquals(task1.getStartTime(), task2.getStartTime());
    }

    @Test
    void checkAddTaskToHistory() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void checkHistoryManagerNotSaveDuplicates() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void checkRecallTaskGoToTheEndOfHistoryList() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        taskManager.getTask(task1Id);
        taskManager.getEpic(epic1Id);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(epic1, history.get(0));
    }

    @Test
    void checkIfDeleteTaskThisTaskDeleteFromHistory() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.deleteTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkIfDeleteSubtaskThisSubtaskDeleteFromHistory() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.getSubTask(subtask1Id);
        taskManager.deleteSubtask(subtask1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkIfDeleteEpicThisEpicAndItsSubtasksDeleteFromHistory() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.deleteEpic(epic1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkHistoryListSaveTheSameOrder() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.getTask(task1Id);
        List<Task> historyFirstCall = taskManager.getHistory();
        List<Task> historySecondCall = taskManager.getHistory();
        assertArrayEquals(historyFirstCall.toArray(), historySecondCall.toArray());
    }

    @Test
    void checkSaveOrderAfterDeleteTaskFromMidOfList() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.getTask(task1Id);
        taskManager.deleteSubtask(subtask1Id);
        List<Task> list = new ArrayList<>(List.of(epic1, task1));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(list.toArray(), history.toArray());
    }

    @Test
    void checkDeleteAllTasksFromHistoryWhenDeleteAllTasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatuses.NEW, duration,
                time.plus(duration));
        int task1Id = taskManager.addNewTask(task1);
        int task2Id = taskManager.addNewTask(task2);
        taskManager.getTask(task1Id);
        taskManager.getTask(task2Id);
        taskManager.deleteTasks();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkDeleteAllSubtasksFromHistoryWhenDeleteAllSubtasks() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW, duration, time.plus(duration));
        Subtask subtask3 = new Subtask(epic1Id, "подзадача 3", "описание подзадачи 3",
                TaskStatuses.NEW, duration, time.plusMinutes(60));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int subtask3Id = taskManager.addNewSubtask(subtask3);
        taskManager.getSubTask(subtask1Id);
        taskManager.getSubTask(subtask2Id);
        taskManager.getSubTask(subtask3Id);
        taskManager.deleteSubtasks();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkDeleteAllEpicsAndSubtasksFromHistoryWhenDeleteAllEpics() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW, duration, time.plus(duration));
        Subtask subtask3 = new Subtask(epic1Id, "подзадача 3", "описание подзадачи 3",
                TaskStatuses.NEW, duration, time.plusMinutes(60));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int subtask3Id = taskManager.addNewSubtask(subtask3);
        taskManager.getSubTask(subtask1Id);
        taskManager.getSubTask(subtask2Id);
        taskManager.getSubTask(subtask3Id);
        taskManager.getEpic(epic1Id);
        taskManager.deleteEpics();
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkAutoChangeStatusOfEpic() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW, duration, time.plus(duration));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        assertEquals(TaskStatuses.NEW, taskManager.getEpic(epic1Id).getStatus());
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "обновленная подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.DONE, duration, time.plus(duration)));
        assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic1Id).getStatus());
        taskManager.updateSubtask(new Subtask(epic1Id, subtask2Id, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.DONE, duration, time.plus(duration)));
        assertEquals(TaskStatuses.DONE, taskManager.getEpic(epic1Id).getStatus());
    }

    @Test
    void checkNewEpicAlwaysHasStatusNew() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 11, 11, 16, 0);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, duration, time);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.DONE, duration, time.plus(duration));
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int epic2Id = taskManager.addNewEpic(taskManager.getEpic(epic1Id));
        assertEquals(TaskStatuses.NEW, taskManager.getEpic(epic2Id).getStatus());
    }

    @Test
    void checkDurationOfEpicIsSumDurationsOfItsSubtasks() {
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
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertEquals(0, epic.getDuration().toMinutes());
    }

    @Test
    void checkStartTimeOfEpicIsStartTimeOfEarliestSubtask() {
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
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertNull(epic.getStartTime());
    }

    @Test
    void checkEndTimeOfEpicIsEndTimeOfLatestSubtask() {
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
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Epic epic = taskManager.getEpic(epic1Id);
        assertNull(epic.getEndTime());
    }

    @Test
    void checkTaskManagersCanGetListOfPrioritizedTasks() {
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, Duration.ofMinutes(30),
                LocalDateTime.now().minusMinutes(60)));
        List<Task> tasks = List.of(taskManager.getSubTask(subtask1Id), taskManager.getTask(task1Id));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertArrayEquals(prioritizedTasks.toArray(), tasks.toArray());
    }

    @Test
    void checkTasksWithoutStartTimeNotAddToListOfPrioritizedTasks() {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), null));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void checkSubtasksWithoutStartTimeNotAddToListOfPrioritizedTasks() {
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1", "Описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), null));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertTrue(prioritizedTasks.isEmpty());
    }

    @Test
    void checkIfTasksOverlapInTimeNewTaskNotAddToManager() {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now()));
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(15)));
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().minusMinutes(15)));
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.size());
    }
}