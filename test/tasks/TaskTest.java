package tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

class TaskTest {
    @Test
    void checkHashCodeForSameTaskIsSame() {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(task1.hashCode(), task1.hashCode());
    }
}