package server;

import com.google.gson.Gson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Managers;
import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    TaskManager taskManager = Managers.getDefault();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void startServer() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    void httpTaskServerCanHandleGetAllTasks() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4)));

        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));

        List<Task> tasks = gson.fromJson(body, new TaskListTypeToken().getType());
        assertEquals(200, code);
        assertEquals(1, tasks.size());
        assertEquals("Задача 1", tasks.getFirst().getTaskName());
    }

    @Test
    void httpTaskServerCanHandleGetTask() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4)));

        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        Task task = gson.fromJson(body, Task.class);
        assertEquals(200, code);
        assertEquals("Задача 1", task.getTaskName());
    }

    @Test
    void httpTaskServerReturnCode400IfGetTaskAndIdInUriNotNumber() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4)));

        URI uri = URI.create("http://localhost:8080/tasks/m");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(400, code);
    }

    @Test
    void httpTaskServerReturnCode404IfTaskNotFound() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4)));

        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanAddTaskToManager() throws IOException, InterruptedException {
        Task task = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4));
        String taskJson = gson.toJson(task);
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        Task task1 = taskManager.getTask(0);
        assertEquals(201, code);
        assertNotNull(task1);
        assertEquals(task1.getTaskName(), task.getTaskName());
    }

    @Test
    void httpTaskServerCantAddEmptyTaskToManager() throws IOException, InterruptedException {
        String taskJson = "{ }";
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(400, code);
    }

    @Test
    void httpTaskServerReturnCode406IfAddTaskAndThisTaskIntersectionWithTasksInManager()
            throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4));
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatuses.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 20));
        taskManager.addNewTask(task1);
        String task2Json = gson.toJson(task2);
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(406, code);
    }

    @Test
    void httpTaskServerCanUpdateTask() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4));
        Task task2 = new Task(0, "Задача 1.1", "Описание задачи 1.1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 1, 2, 3, 34));
        taskManager.addNewTask(task1);
        String task2Json = gson.toJson(task2);
        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(task2Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(201, code);
        assertEquals(task2, taskManager.getTask(0));
        assertEquals(task2.getDuration(), taskManager.getTask(0).getDuration());
        assertEquals(task2.getStartTime(), taskManager.getTask(0).getStartTime());
    }

    @Test
    void httpTaskServerCantUpdateTaskIfTaskNotFound() throws IOException, InterruptedException {
        Task task = new Task(0, "Задача 1.1", "Описание задачи 1.1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 1, 2, 3, 4));
        String taskJson = gson.toJson(task);
        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCantUpdateTaskIfTaskHaveIntersections() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4));
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatuses.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 40));
        Task task3 = new Task(1, "Задача 2.1", "Описание задачи 2.1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 1, 2, 3, 10));
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        String task3Json = gson.toJson(task3);
        URI uri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(task3Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(406, code);
    }

    @Test
    void httpTaskServerCanDeleteTask() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 1, 2, 3, 4)));

        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(201, code);
        assertTrue(taskManager.getTasksList().isEmpty());
    }

    @Test
    void httpTaskServerReturnCode404IfDeleteNonExistentTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/0");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);

        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        List<Subtask> subtasks = gson.fromJson(body, new SubtaskListTypeToken().getType());
        assertEquals(200, code);
        assertEquals(1, subtasks.size());
        assertEquals("подзадача 1", subtasks.getFirst().getTaskName());
    }

    @Test
    void httpTaskServerCanGetSubtaskById() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);

        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        Subtask subtask2 = gson.fromJson(body, Subtask.class);
        assertEquals(200, code);
        assertNotNull(subtask2);
        assertEquals(subtask1.getTaskName(), subtask2.getTaskName());
    }

    @Test
    void httpTaskServerReturnCode404IfTryGetNotExistentSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerReturnCode400IfIdInUriNotNumber() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/c");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(400, code);
    }

    @Test
    void httpTaskServerCanAddSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        String subtask1Json = gson.toJson(subtask1);
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(201, code);
        assertFalse(subtasks.isEmpty());
        assertEquals(subtask1.getTaskName(), subtasks.getFirst().getTaskName());
    }

    @Test
    void httpTaskServerCantAddSubtaskWithEmptyBody() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        String subtask1Json = "{ }";
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(400, code);
    }

    @Test
    void httpTaskServerReturnCode404IfTryAddSubtasksToNotExistentEpic() throws IOException, InterruptedException {
        Subtask subtask1 = new Subtask(1, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        String subtask1Json = gson.toJson(subtask1);
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(404, code);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void httpTaskServerReturnCode406IfNewSubtaskIntersectionWithTasksInManager() throws IOException, InterruptedException {
        taskManager.addNewTask(new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 5, 9, 50)));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        String subtask1Json = gson.toJson(subtask1);
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask1Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        List<Subtask> subtasks = taskManager.getSubtasksList();
        assertEquals(406, code);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void httpTaskServerCanUpdateSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(epic1Id, 1, "подзадача 1.1", "описание подзадачи 1.1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 30));
        String subtask2Json = gson.toJson(subtask2);
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(201, code);
        assertEquals(subtask2.getTaskName(), taskManager.getSubTask(1).getTaskName());
    }

    @Test
    void httpTaskServerReturnCode404IfTryUpdateNotExistentSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask2 = new Subtask(epic1Id, 1, "подзадача 1.1", "описание подзадачи 1.1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        String subtask2Json = gson.toJson(subtask2);
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask2Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void httpTaskServerReturnCode406IfUpdateSubtaskHaveIntersection() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        Subtask subtask2 = new Subtask(epic1Id, "подзадача 2", "описание подзадачи 2",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 40));
        taskManager.addNewSubtask(subtask2);
        Subtask subtask3 = new Subtask(epic1Id, 1, "подзадача 1.1",
                "описание подзадачи 1.1", TaskStatuses.NEW, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 20));
        String subtask3Json = gson.toJson(subtask3);
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(subtask3Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(406, code);
        assertNotEquals(taskManager.getSubTask(1).getTaskName(), subtask3.getTaskName());
    }

    @Test
    void httpTaskServerCanDeleteSubtask() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(201, code);
        assertTrue(taskManager.getSubtasksList().isEmpty());
    }

    @Test
    void httpTaskServerReturnCode404IfTryDeleteNotExistentSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewEpic(epic1);
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        List<Epic> epics = gson.fromJson(body, new EpicListTypeToken().getType());
        assertEquals(200, code);
        assertEquals(1, epics.size());
    }

    @Test
    void httpTaskServerCanGetEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewEpic(epic1);
        URI uri = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        Epic epic2 = gson.fromJson(body, Epic.class);
        assertEquals(200, code);
        assertEquals(epic1.getTaskName(), epic2.getTaskName());
    }

    @Test
    void httpTaskServerReturnCode404IfTryGetNotExistentEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanGetEpicSubtasks() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS, Duration.ofMinutes(30),
                LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        List<Subtask> subtasksOfEpic = gson.fromJson(body, new SubtaskListTypeToken().getType());
        assertEquals(200, code);
        assertArrayEquals(taskManager.getEpicSubtasks(epic1Id).toArray(), subtasksOfEpic.toArray());
    }

    @Test
    void httpTaskServerReturn404IfTryGetSubtasksOfNotExistentEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanAddEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        String epic1Json = gson.toJson(epic1);
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(epic1Json))
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        Epic epic2 = taskManager.getEpic(0);
        assertEquals(201, code);
        assertEquals(epic1.getTaskName(), epic2.getTaskName());
    }

    @Test
    void httpTaskServerCanDeleteEpic() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewEpic(epic1);
        URI uri = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(201, code);
        assertTrue(taskManager.getEpicsList().isEmpty());
    }

    @Test
    void httpTaskServerReturnCode404IfTryDeleteNotExistentEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0");
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        assertEquals(404, code);
    }

    @Test
    void httpTaskServerCanGetHistoryList() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 2, 16, 0));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewTask(task1);
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        taskManager.getTask(0);
        taskManager.getEpic(1);
        taskManager.getSubTask(2);
        URI uri = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        String historyJson = gson.toJson(taskManager.getHistory());
        assertEquals(200, code);
        assertEquals(historyJson, body);
    }

    @Test
    void httpTaskServerCanGetPrioritizedTasksList() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 2, 16, 0));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewTask(task1);
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .header("Accept", "application/json")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);
        int code = response.statusCode();
        String body = Arrays.stream(response.body().split("\n")).skip(1)
                .collect(Collectors.joining("\n"));
        String prioritizedJson = gson.toJson(taskManager.getPrioritizedTasks());
        assertEquals(200, code);
        assertEquals(prioritizedJson, body);
    }
}