package services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @BeforeEach
    public void initManager() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        taskManager = new FileBackedTaskManager(file);
    }

    @Test
    void canSaveEmptyFileBackedTaskManager() throws IOException {
        File file = taskManager.getPath().toFile();
        taskManager.save();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void canAddTaskAndSaveThisTaskInFile() {
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2022, 11, 11, 16, 0);
        File file = taskManager.getPath().toFile();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW, duration, time);
        int task1Id = taskManager.addNewTask(task1);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task task2 = fileBackedTaskManager.getTask(task1Id);
        assertEquals(task2, task1);
        assertEquals(task2.getDuration(), task1.getDuration());
        assertEquals(task2.getStartTime(), task1.getStartTime());
    }

    @Test
    void canAddEpicAndSaveThisEpicInFile() {
        File file = taskManager.getPath().toFile();
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic2 = fileBackedTaskManager.getEpic(epic1Id);
        assertEquals(epic2, epic1);
        assertArrayEquals(epic2.getSubtaskIds().toArray(), epic1.getSubtaskIds().toArray());
        assertEquals(epic2.getDuration(), epic1.getDuration());
        assertEquals(epic2.getStartTime(), epic1.getStartTime());
        assertEquals(epic2.getEndTime(), epic1.getEndTime());
    }

    @Test
    void canAddSubtaskAndSaveThisSubtaskInFile() {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2022, 11, 11, 16, 0);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "Описание подзадачи 1",
                TaskStatuses.NEW, duration, time);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        Subtask subtask2 = fileBackedTaskManager.getSubTask(subtask1Id);
        assertEquals(subtask2, subtask1);
        assertEquals(subtask2.getIdOfEpic(), subtask1.getIdOfEpic());
        assertEquals(subtask2.getDuration(), subtask1.getDuration());
        assertEquals(subtask2.getStartTime(), subtask1.getStartTime());
    }

    @Test
    void checkWhenDeleteTaskThisTaskDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2022, 11, 11, 16, 0);
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.NEW, duration, time));
        taskManager.deleteTask(task1Id);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void checkWhenDeleteSubtaskThisSubtaskDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, duration, time));
        taskManager.deleteSubtask(subtask1Id);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(2, count);
    }

    @Test
    void checkWhenDeleteEpicThisEpicDeleteFromFileAndSubtasksOfThisEpicDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, duration, time));
        taskManager.deleteEpic(epic1Id);
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void checkWhenDeleteAllTasksAllTasksDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                duration, time));
        taskManager.deleteTasks();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void checkWhenDeleteAllSubtasksAllSubtasksDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, duration, time));
        taskManager.deleteSubtasks();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(2, count);
    }

    @Test
    void checkWhenDeleteAllEpicsAllEpicsAndAllSubtasksDeleteFromFile() throws IOException {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, duration, time));
        taskManager.deleteEpics();
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    void checkFileBackedTaskManagerCanBeLoadedFromFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(taskManager);
    }

    @Test
    void checkFileBackedTaskManagerCanUpdateTaskAndSaveThisInFile() {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2020, 10, 10, 15, 0);
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS, duration, time));
        taskManager.updateTask(new Task(task1Id, "Задача 1", "Обновленная задача 1",
                TaskStatuses.DONE, duration, time.plus(duration)));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task task1 = taskManager.getTask(task1Id);
        Task task2 = newTaskManager.getTask(task1Id);
        assertEquals(task2, task1);
        assertEquals(task2.getDuration(), task1.getDuration());
        assertEquals(task2.getStartTime(), task1.getStartTime());
    }

    @Test
    void checkFileBackedTaskManagerCanUpdateEpicAndSaveThisInFile() {
        File file = taskManager.getPath().toFile();
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.updateEpic(new Epic(epic1Id, "Эпик 1", "Обновленное описание эпика 1"));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic1 = taskManager.getEpic(epic1Id);
        Epic epic2 = newTaskManager.getEpic(epic1Id);
        assertEquals(epic2, epic1);
        assertArrayEquals(epic2.getSubtaskIds().toArray(), epic1.getSubtaskIds().toArray());
        assertEquals(epic2.getDuration(), epic1.getDuration());
        assertEquals(epic2.getStartTime(), epic1.getStartTime());
        assertEquals(epic2.getEndTime(), epic1.getEndTime());
    }

    @Test
    void checkFileBackedManagerCanUpdateSubtaskAndSaveThisInFile() {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2021, 6, 20, 11, 0);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW, duration, time));
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "Подзадача 1",
                "Обновленная подзадача 1", TaskStatuses.DONE, duration, time.plus(duration)));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Subtask subtask1 = taskManager.getSubTask(subtask1Id);
        Subtask subtask2 = newTaskManager.getSubTask(subtask1Id);
        assertEquals(subtask2, subtask1);
        assertEquals(subtask2.getIdOfEpic(), subtask1.getIdOfEpic());
        assertEquals(subtask2.getDuration(), subtask1.getDuration());
        assertEquals(subtask2.getStartTime(), subtask1.getStartTime());
    }

    @Test
    void checkFileBackedManagerRestoreNumberOfNextTaskId() {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.now();
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                duration, time));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        int task2Id = newTaskManager.addNewTask(new Task("Задача 2", "Описание задачи 2",
                TaskStatuses.IN_PROGRESS, duration, time.plus(duration)));
        assertEquals(1, task2Id);
    }

    @Test
    void checkFileBackedManagerRestorePrioritizedTaskListFromFile() {
        File file = taskManager.getPath().toFile();
        Duration duration = Duration.ofMinutes(30);
        LocalDateTime time = LocalDateTime.of(2021, 12, 31, 22, 15);
        taskManager.addNewTask(new Task("Задача 3", "Описание задачи 3", TaskStatuses.NEW,
                duration, time.plusMinutes(70)));
        taskManager.addNewTask(new Task("Задача 2", "Описание задачи 2", TaskStatuses.NEW,
                duration, time.plusMinutes(35)));
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.IN_PROGRESS,
                duration, time));
        List<Task> list1 = taskManager.getPrioritizedTasks();
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);
        List<Task> list2 = fileBackedTaskManager.getPrioritizedTasks();
        assertArrayEquals(list1.toArray(), list2.toArray());
    }

    @Test
    void checkSaveToFileCanThrowManagerSaveException() {
        File file = taskManager.getPath().toFile();
        file.setWritable(false);
        assertThrows(ManagerSaveException.class, taskManager::save);
    }

    @Test
    void checkLoadFromFileCanThrowManagerSaveException() {
        assertThrows(ManagerLoadException.class, () ->
                FileBackedTaskManager.loadFromFile(new File("src//test.CSV")));
    }
}