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
    void managerCanGetListsOfAllTasks() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        List<Task> tasks = taskManager.getTasksList();
        assertFalse(tasks.isEmpty());
        List<Epic> epics = taskManager.getEpicsList();
        assertFalse(epics.isEmpty());
        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void checkManagerCanGetEpicSubtasks() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        Integer subtask2Id = taskManager.addNewSubtask(subtask2);
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic1Id);
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void cantGetSubtasksOfNonExistentEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        List<Subtask> subtasks = taskManager.getEpicSubtasks(1);
        assertNull(subtasks);
    }

    @Test
    void returnNullIfTryAddSubtaskToNonExistentEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Subtask subtask1 = new Subtask(1, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        assertNull(subtask1Id);
    }

    @Test
    void cantGetNonExistentTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNull(taskManager.getTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentSubtask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNull(taskManager.getSubTask(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantGetNonExistentEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        assertNull(taskManager.getEpic(1));
        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void cantUpdateNonExistentTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        taskManager.updateTask(new Task(1, "Обновленная задача 2",
                "новое описание задачи 2", TaskStatuses.IN_PROGRESS));
        assertNull(taskManager.getTask(1));
    }

    @Test
    void cantUpdateNonExistentEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        taskManager.updateEpic(new Epic(1, "Эпик2new", "Новое описание эпика 2"));
        assertNull(taskManager.getEpic(1));
    }

    @Test
    void cantUpdateNonExistentSubtask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        taskManager.updateSubtask(new Subtask(epic1Id, 3, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.NEW));
        assertNull(taskManager.getSubTask(3));
    }

    @Test
    void checkAutoChangeStatusOfEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        Integer subtask2Id = taskManager.addNewSubtask(subtask2);
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "обновленная подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.DONE));
        assertEquals(TaskStatuses.IN_PROGRESS, taskManager.getEpic(epic1Id).getStatus());
        taskManager.updateSubtask(new Subtask(epic1Id, subtask2Id, "обновленная подзадача 2",
                "Новое описание подзадачи 2", TaskStatuses.DONE));
        assertEquals(TaskStatuses.DONE, taskManager.getEpic(epic1Id).getStatus());
    }

    @Test
    void checkNewEpicAlwaysHasStatusNew() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.DONE);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        Integer subtask2Id = taskManager.addNewSubtask(subtask2);
        int epic2Id = taskManager.addNewEpic(taskManager.getEpic(epic1Id));
        assertEquals(TaskStatuses.NEW, taskManager.getEpic(epic2Id).getStatus());
    }

    @Test
    void checkManagerCanUpdateTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.updateTask(new Task(task1Id, "Задача 1",
                "Новое описание задачи 1", TaskStatuses.NEW));
        Task task = taskManager.getTask(task1Id);
        assertNotEquals(task, task1);
    }

    @Test
    void checkManagerCanUpdateEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
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
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "подзадача 1",
                "Новое описание подзадачи 1", TaskStatuses.NEW));
        Subtask subtask = taskManager.getSubTask(subtask1Id);
        assertNotEquals(subtask, subtask1);
    }

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
    void checkManagerCanCreateTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertNotNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanDeleteTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        taskManager.deleteTask(task1Id);
        assertNull(taskManager.getTask(task1Id));
    }

    @Test
    void checkManagerCanCreateEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        assertNotNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanDeleteEpicAndItsSubtasks() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteEpic(epic1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertNull(taskManager.getEpic(epic1Id));
    }

    @Test
    void checkManagerCanCreateSubtask() {
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
    void checkManagerCanDeleteSubtaskAndItsIdDeleteFromItsEpic() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        taskManager.deleteSubtask(subtask1Id);
        assertNull(taskManager.getSubTask(subtask1Id));
        assertTrue(taskManager.getEpic(epic1Id).getSubtaskIds().isEmpty());
    }

    @Test
    void CheckManagerCanDeleteAllTasks() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        Task task2 = new Task("Задача 2", "Простая задача 2", TaskStatuses.NEW);
        int task2Id = taskManager.addNewTask(task2);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasksList().isEmpty());
    }

    @Test
    void checkManagerCanDeleteAllEpics() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
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
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        taskManager.deleteSubtasks();
        assertTrue(taskManager.getSubtasksList().isEmpty());
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
    void checkHistoryManagerSavePreviousVersionOfTask() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
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