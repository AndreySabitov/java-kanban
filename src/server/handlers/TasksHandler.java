package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.ManagerSaveException;
import services.NotFoundException;
import services.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case ADD_TASK:
                handleAddTask(exchange);
                break;
            case UPDATE_TASK:
                handleUpdateTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            case UNKNOWN:
                sendNotFound(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        int rCode = 200;
        String response;
        List<Task> tasksList = taskManager.getTasksList();
        response = gson.toJson(tasksList);
        sendText(exchange, response, rCode);
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getTaskId(exchange);
        String response;
        int rCode;
        if (id.isPresent()) {
            try {
                Task task = taskManager.getTask(id.get());
                response = gson.toJson(task);
                rCode = 200;
                sendText(exchange, response, rCode);
            } catch (NotFoundException e) {
                response = e.getMessage();
                rCode = 404;
                sendNotFound(exchange, response, rCode);
            }
        } else {
            rCode = 400;
            response = "Некорректный id";
            sendNotFound(exchange, response, rCode);
        }
    }

    private void handleAddTask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        int id = taskManager.addNewTask(task);
        if (id == -1) {
            sendInteractions(exchange);
        } else {
            rCode = 201;
            response = "Задача успешно создана";
            sendText(exchange, response, rCode);
        }
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            try {
                int id = idOptional.get();
                taskManager.getTask(id);
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(body, Task.class);
                if (task.getTaskId() != null && task.getTaskId() == id) {
                    try {
                        taskManager.updateTask(task);
                        rCode = 201;
                        response = "Задача обновлена";
                        sendText(exchange, response, rCode);
                    } catch (ManagerSaveException e) {
                        sendInteractions(exchange);
                    }
                } else {
                    rCode = 400;
                    response = "id задачи не задан или задан некорректно";
                    sendNotFound(exchange, response, rCode);
                }
            } catch (NotFoundException e) {
                rCode = 404;
                response = e.getMessage();
                sendNotFound(exchange, response, rCode);
            }
        } else {
            rCode = 400;
            response = "Некорректный id";
            sendNotFound(exchange, response, rCode);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            int id = idOptional.get();
            try {
                taskManager.deleteTask(id);
                rCode = 201;
                response = "Задача удалена";
                sendText(exchange, response, rCode);
            } catch (NotFoundException e) {
                rCode = 404;
                response = e.getMessage();
                sendNotFound(exchange, response, rCode);
            }
        } else {
            rCode = 400;
            response = "Некорректный id";
            sendNotFound(exchange, response, rCode);
        }
    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (method.equals("GET") && parts.length == 2) {
            return Endpoint.GET_TASKS;
        } else if (method.equals("GET") && parts.length == 3) {
            return Endpoint.GET_TASK;
        } else if (method.equals("POST") && parts.length == 2) {
            return Endpoint.ADD_TASK;
        } else if (method.equals("POST") && parts.length == 3) {
            return Endpoint.UPDATE_TASK;
        } else if (method.equals("DELETE") && parts.length == 3) {
            return Endpoint.DELETE_TASK;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}
