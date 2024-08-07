package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.ManagerSaveException;
import services.NotFoundException;
import services.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(exchange);
                break;
            case ADD_SUBTASK:
                handleAddSubtask(exchange);
                break;
            case UPDATE_SUBTASK:
                handleUpdateSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            case UNKNOWN:
                sendNotFound(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        int rCode = 200;
        String response;
        List<Subtask> subtasksList = taskManager.getSubtasksList();
        response = gson.toJson(subtasksList);
        sendText(exchange, response, rCode);
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getTaskId(exchange);
        String response;
        int rCode;
        if (id.isPresent()) {
            try {
                Subtask subtask = taskManager.getSubTask(id.get());
                response = gson.toJson(subtask);
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

    private void handleAddSubtask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        try {
            taskManager.getEpic(subtask.getIdOfEpic());
            int id = taskManager.addNewSubtask(subtask);
            if (id == -1) {
                sendInteractions(exchange);
            } else {
                rCode = 201;
                response = "Задача успешно создана";
                sendText(exchange, response, rCode);
            }
        } catch (NotFoundException e) {
            rCode = 404;
            response = e.getMessage() + "невозможно добавить подзадачу без эпика";
            sendNotFound(exchange, response, rCode);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            int id = idOptional.get();
            try {
                taskManager.getSubTask(id);
                InputStream inputStream = exchange.getRequestBody();
                String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                if (subtask.getTaskId() != null && subtask.getTaskId() == id) {
                    try {
                        taskManager.updateSubtask(subtask);
                        rCode = 201;
                        response = "Задача обновлена";
                        sendText(exchange, response, rCode);
                    } catch (ManagerSaveException e) {
                        sendInteractions(exchange);
                    }
                } else {
                    rCode = 400;
                    response = "id подзадачи не задан или задан некорректно";
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

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            int id = idOptional.get();
            try {
                taskManager.deleteSubtask(id);
                rCode = 201;
                response = "Подадача удалена";
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
            return Endpoint.GET_SUBTASKS;
        } else if (method.equals("GET") && parts.length == 3) {
            return Endpoint.GET_SUBTASK;
        } else if (method.equals("POST") && parts.length == 2) {
            return Endpoint.ADD_SUBTASK;
        } else if (method.equals("POST") && parts.length == 3) {
            return Endpoint.UPDATE_SUBTASK;
        } else if (method.equals("DELETE") && parts.length == 3) {
            return Endpoint.DELETE_SUBTASK;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}
