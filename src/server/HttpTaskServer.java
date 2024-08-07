package server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import server.handlers.*;
import services.Managers;
import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatuses;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer taskServer;
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer() throws IOException {
        taskManager = Managers.getDefault();

        taskServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        taskServer.createContext("/tasks", new TasksHandler(taskManager));
        taskServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        taskServer.createContext("/epics", new EpicsHandler(taskManager));
        taskServer.createContext("/history", new HistoryHandler(taskManager));
        taskServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        taskServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        taskServer.createContext("/tasks", new TasksHandler(taskManager));
        taskServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        taskServer.createContext("/epics", new EpicsHandler(taskManager));
        taskServer.createContext("/history", new HistoryHandler(taskManager));
        taskServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static Gson getGson() {
        return gson;
    }

    public void start() {
        System.out.println("Сервер запущен на порту: " + PORT);
        taskServer.start();
    }

    public void stop() {
        System.out.println("Сервер остановлен");
        taskServer.stop(0);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatuses.NEW,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 2, 16, 0));
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        taskManager.addNewTask(task1);
        int epic1Id = taskManager.addNewEpic(epic1);
        Subtask subtask1 = new Subtask(epic1Id, "подзадача 1", "описание подзадачи 1",
                TaskStatuses.IN_PROGRESS,
                Duration.ofMinutes(30), LocalDateTime.of(2020, 10, 5, 10, 0));
        taskManager.addNewSubtask(subtask1);


        HttpTaskServer taskServer = new HttpTaskServer(taskManager);

        taskServer.start();
    }
}
