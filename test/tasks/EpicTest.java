package tasks;

import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {
    @Test
    void epicCanSaveSubtaskId() {
        TaskManager taskManager = Managers.getDefault();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        assertFalse(epic1.getSubtaskIds().isEmpty());
    }

    @Test
    void epicCanCleanSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        Epic epic1 = new Epic("Эпик 1", "Сложный эпик 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        epic1.cleanSubtaskIds();
        assertTrue(epic1.getSubtaskIds().isEmpty());
    }
}