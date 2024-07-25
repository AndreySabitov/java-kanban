package tasks;

import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {
    @Test
    void checkHashCodeForSameTaskIsSame() {
        TaskManager taskManager = Managers.getDefault();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        Task task1 = new Task("Задача 1", "Простая задача 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        assertEquals(task1.hashCode(), task1.hashCode());
    }
}