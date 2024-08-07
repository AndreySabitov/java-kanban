package server.handlers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.NotFoundException;
import services.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case ADD_EPIC:
                handleAddEpic(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpic(exchange);
                break;
            case UNKNOWN:
                sendNotFound(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        List<Epic> epicsList = taskManager.getEpicsList();
        response = gson.toJson(epicsList);
        rCode = 200;
        sendText(exchange, response, rCode);
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getTaskId(exchange);
        String response;
        int rCode;
        if (id.isPresent()) {
            try {
                Epic epic = taskManager.getEpic(id.get());
                response = gson.toJson(epic);
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

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            try {
                List<Subtask> subtasksOfEpic = taskManager.getEpicSubtasks(idOptional.get());
                rCode = 200;
                response = gson.toJson(subtasksOfEpic);
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

    private void handleAddEpic(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(body);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String name = jsonObject.get("taskName").getAsString();
        String description = jsonObject.get("taskDescription").getAsString();
        Epic epic = new Epic(name, description);
        taskManager.addNewEpic(epic);
        rCode = 201;
        response = "Задача успешно создана";
        sendText(exchange, response, rCode);
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int rCode;
        String response;
        Optional<Integer> idOptional = getTaskId(exchange);
        if (idOptional.isPresent()) {
            int id = idOptional.get();
            try {
                taskManager.deleteEpic(id);
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
            return Endpoint.GET_EPICS;
        } else if (method.equals("GET") && parts.length == 3) {
            return Endpoint.GET_EPIC;
        } else if (method.equals("GET") && parts.length == 4 && parts[3].equals("subtasks")) {
            return Endpoint.GET_EPIC_SUBTASKS;
        } else if (method.equals("POST") && parts.length == 2) {
            return Endpoint.ADD_EPIC;
        } else if (method.equals("DELETE") && parts.length == 3) {
            return Endpoint.DELETE_EPIC;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}
