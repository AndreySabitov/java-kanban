package tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

class EpicTest {
    @Test
    void epicCanSaveSubtaskId() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        assertFalse(epic1.getSubtaskIds().isEmpty());
    }

    @Test
    void epicCanCleanSubtasks() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW);
        Integer subtask1Id = taskManager.addNewSubtask(subtask1);
        epic1.cleanSubtaskIds();
        assertTrue(epic1.getSubtaskIds().isEmpty());
    }
}