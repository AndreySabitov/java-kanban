package services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.util.ArrayList;
import java.util.List;

class InMemoryTaskManagerTest {
    @Test
    void managerCanGetListsOfAllTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
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
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic1Id);
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void cantGetSubtasksOfNonExistentEpic() {
        TaskManager taskManager = Managers.getDefault();
        List<Subtask> subtasks = taskManager.getEpicSubtasks(1);
        assertNull(subtasks);
    }

    @Test
    void returnMinusOneIfTryAddSubtaskToNonExistentEpic() {
        TaskManager taskManager = Managers.getDefault();
        Subtask subtask1 = new Subtask(1, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertEquals(-1, subtask1Id);
    }

    @Test
    void cantGetNonExistentTask() {
        TaskManager taskManager = Managers.getDefault();
        assertNull(taskManager.getTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentSubtask() {
        TaskManager taskManager = Managers.getDefault();
        assertNull(taskManager.getSubTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentEpic() {
        TaskManager taskManager = Managers.getDefault();
        assertNull(taskManager.getEpic(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantUpdateNonExistentTask() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.updateTask(new Task(1, "Обновленная задача 2",
                "новое описание задачи 2", TaskStatuses.IN_PROGRESS));
        assertNull(taskManager.getTask(1));
    }

    @Test
    void cantUpdateNonExistentEpic() {
        TaskManager taskManager = Managers.getDefault();
        taskManager.updateEpic(new Epic(1, "Эпик2new", "Новое описание эпика 2"));
        assertNull(taskManager.getEpic(1));
    }

    @Test
    void cantUpdateNonExistentSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        taskManager.updateSubtask(new Subtask(epic1Id, 3, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.NEW));
        assertNull(taskManager.getSubTask(3));
    }

    @Test
    void checkAutoChangeStatusOfEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "обновленная подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.DONE));
        assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic1Id).getStatus());
        taskManager.updateSubtask(new Subtask(epic1Id, subtask2Id, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.DONE));
        assertEquals(TaskStatuses.DONE, taskManager.getEpic(epic1Id).getStatus());
    }

    @Test
    void checkNewEpicAlwaysHasStatusNew() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.DONE);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        int subtask2Id = taskManager.addNewSubtask(subtask2);
        int epic2Id = taskManager.addNewEpic(taskManager.getEpic(epic1Id));
        assertEquals(TaskStatuses.NEW, taskManager.getEpic(epic2Id).getStatus());
    }

    @Test
    void checkManagerCanUpdateTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.updateTask(new Task(task1Id, "Задача 1",
                "Новое описание задачи 1", TaskStatuses.NEW));
        Task task = taskManager.getTask(task1Id);
        assertNotEquals(task, task1);
    }

    @Test
    void checkManagerCanUpdateEpic() {
        TaskManager taskManager = Managers.getDefault();
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
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.NEW));
        Subtask subtask = taskManager.getSubTask(subtask1Id);
        assertNotEquals(subtask, subtask1);
    }

    @Test
    void tasksWithSameIdIsSame() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(taskManager.getTask(task1Id), taskManager.getTask(task1Id));
    }

    @Test
    void epicsWithSameIdIsSame() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertEquals(taskManager.getEpic(epic1Id), taskManager.getEpic(epic1Id));
    }

    @Test
    void subtasksWithSameIdIsSame() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertEquals(taskManager.getSubTask(subtask1Id), taskManager.getSubTask(subtask1Id));
    }

    @Test
    void cantMakeEpicYourOwnSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask = new Subtask(epic1Id, epic1Id, "эпик подзадача",
                "пытаюсь сделать эпик подзадачей", TaskStatuses.NEW);
        int subtaskEpicId = taskManager.addNewSubtask(subtask);
        assertEquals(-1, subtaskEpicId);
    }

    @Test
    void cantMakeSubtaskYourOwnEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Subtask subtaskOfSubtask = new Subtask(subtask1Id, "подзадача - Эпик",
                "хочу сделать подзадачу своим эпиком", TaskStatuses.NEW);
        int epicSubtaskId = taskManager.addNewSubtask(subtaskOfSubtask);
        assertEquals(-1, epicSubtaskId);
    }

    @Test
    void checkManagerCanCreateTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertNotNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanDeleteTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.deleteTask(task1Id);
        assertNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanCreateEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanDeleteEpicAndItsSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpic(epic1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanCreateSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertNotNull(taskManager.getSubTask(subtask1Id));
    }

    @Test
    void checkManagerCanDeleteSubtaskAndItsIdDeleteFromItsEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteSubtask(subtask1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertTrue(taskManager.getEpic(epic1Id).getSubtaskIds().isEmpty());
    }

    @Test
    void CheckManagerCanDeleteAllTasks() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task("Задача 2", "Простая задача 2", TaskStatuses.NEW);
        int task2Id = taskManager.addNewTask(task2);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasksList().isEmpty());
    }

    @Test
    void checkManagerCanDeleteAllEpics() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpics();
        assertTrue(taskManager.getEpicsList().isEmpty());
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void checkManagerCanDeleteAllSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void checkNoConflictBetweenTasksWithGeneratedIdAndWithGivenId() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task(task1Id, "задача 2", "задача 2 с таким же id", TaskStatuses.NEW);
        int task2Id = taskManager.addNewTask(task2);
        assertTrue(task1Id != task2Id);
    }

    @Test
    void checkTaskNotChangeWhenAddedToManager() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(task1, taskManager.getTask(task1Id));
    }

    @Test
    void checkHistoryManagerSavePreviousVersionOfTask() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.updateTask(new Task(task1Id, "Обновленная задача 1",
                "новое описание задачи 1", TaskStatuses.IN_PROGRESS));
        List<Task> history = taskManager.getHistory();
        assertEquals(task1, history.get(0));
    }

    @Test
    void checkAddTaskToHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void checkHistoryManagerNotSaveDuplicates() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.getTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void checkRecallTaskGoToTheEndOfHistoryList() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
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
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getTask(task1Id);
        taskManager.deleteTask(task1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkIfDeleteSubtaskThisSubtaskDeleteFromHistory() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.getSubTask(subtask1Id);
        taskManager.deleteSubtask(subtask1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkIfDeleteEpicThisEpicAndItsSubtasksDeleteFromHistory() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.deleteEpic(epic1Id);
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void checkHistoryListSaveTheSameOrder() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
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
        TaskManager taskManager = Managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        taskManager.getTask(task1Id);
        taskManager.deleteSubtask(subtask1Id);
        List<Task> list = new ArrayList<>(List.of(epic1, task1));
        List<Task> history = taskManager.getHistory();
        assertArrayEquals(list.toArray(), history.toArray());
    }
}