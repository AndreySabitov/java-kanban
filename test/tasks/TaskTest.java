package tasks;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

class TaskTest {
    @Test
    void checkHashCodeForSameTaskIsSame() {
        Managers managers = new Managers();
        TaskManager taskManager = managers.getDefault();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW);
        int task1Id = taskManager.addNewTask(task1);
        assertTrue(task1.hashCode() == task1.hashCode());
    }
}