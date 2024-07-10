package services;

import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    @Test
    void canSaveEmptyFileBackedTaskManager() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        FileBackedTaskManager taskManager = new FileBackedTaskManager(file);
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
    void canAddTaskAndSaveThisTaskInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW));
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
    void canAddEpicAndSaveThisEpicInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
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
    void canAddSubtaskAndSaveThisSubtaskInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1", "Описание подзадачи 1",
                TaskStatuses.NEW));
        int count = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while (br.ready()) {
                br.readLine();
                count++;
            }
        }
        assertEquals(3, count);
    }

    @Test
    void checkWhenDeleteTaskThisTaskDeleteFromFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.NEW));
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
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
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
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
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
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW));
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
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
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
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
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
    void checkTaskNotChangeWhenLoadToFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Task task1 = new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS);
        int task1Id = taskManager.addNewTask(task1);
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task task2 = newTaskManager.getTask(task1Id);
        assertEquals(task2, task1);
    }

    @Test
    void checkEpicNotChangeWhenLoadToFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1ID = taskManager.addNewEpic(epic1);
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic2 = newTaskManager.getEpic(epic1ID);
        assertEquals(epic2, epic1);
        assertArrayEquals(epic2.getSubtaskIds().toArray(), epic1.getSubtaskIds().toArray());
    }

    @Test
    void checkSubtaskNotChangeWhenLoadToFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1",
                "Описание подзадачи 1", TaskStatuses.DONE);
        int subtask1Id = taskManager.addNewSubtask(subtask1);
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Subtask subtask2 = newTaskManager.getSubTask(subtask1Id);
        assertEquals(subtask2, subtask1);
        assertEquals(subtask2.getIdOfEpic(), subtask1.getIdOfEpic());
    }

    @Test
    void checkFileBackedTaskManagerCanUpdateTaskAndSaveThisInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.IN_PROGRESS));
        taskManager.updateTask(new Task(task1Id, "Задача 1", "Обновленная задача 1",
                TaskStatuses.DONE));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Task task1 = taskManager.getTask(task1Id);
        Task task2 = newTaskManager.getTask(task1Id);
        assertEquals(task2, task1);
    }

    @Test
    void checkFileBackedTaskManagerCanUpdateEpicAndSaveThisInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        taskManager.updateEpic(new Epic(epic1Id, "Эпик 1", "Обновленное описание эпика 1"));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Epic epic1 = taskManager.getEpic(epic1Id);
        Epic epic2 = newTaskManager.getEpic(epic1Id);
        assertEquals(epic2, epic1);
    }

    @Test
    void checkFileBackedManagerCanUpdateSubtaskAndSaveThisInFile() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
        taskManager.updateSubtask(new Subtask(epic1Id, subtask1Id, "Подзадача 1",
                "Обновленная подзадача 1", TaskStatuses.DONE));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        Subtask subtask1 = taskManager.getSubTask(subtask1Id);
        Subtask subtask2 = newTaskManager.getSubTask(subtask1Id);
        assertEquals(subtask2, subtask1);
    }

    @Test
    void checkFileBackedManagerCanGetEpicSubtasks() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.NEW));
        List<Subtask> subtasks = taskManager.getEpicSubtasks(epic1Id);
        assertFalse(subtasks.isEmpty());
    }

    @Test
    void checkFileBackedManagerCanGetHistory() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        int task1Id = taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1",
                TaskStatuses.NEW));
        int epic1Id = taskManager.addNewEpic(new Epic("Эпик 1", "Описание эпика 1"));
        int subtask1Id = taskManager.addNewSubtask(new Subtask(epic1Id, "Подзадача 1",
                "Описание подзадачи 1", TaskStatuses.IN_PROGRESS));
        taskManager.getTask(task1Id);
        taskManager.getEpic(epic1Id);
        taskManager.getSubTask(subtask1Id);
        List<Task> tasksHistory = taskManager.getHistory();
        assertEquals(3, tasksHistory.size());
    }

    @Test
    void checkFileBackedManagerRestoreNumberOfNextTaskId() throws IOException {
        File file = File.createTempFile("test", ".CSV");
        TaskManager taskManager = new FileBackedTaskManager(file);
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW));
        TaskManager newTaskManager = FileBackedTaskManager.loadFromFile(file);
        int task2Id = newTaskManager.addNewTask(new Task("Задача 2", "Описание задачи 2",
                TaskStatuses.IN_PROGRESS));
        assertEquals(1, task2Id);
    }
}