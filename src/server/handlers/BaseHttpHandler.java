package server.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.HttpTaskServer;
import services.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    TaskManager taskManager;
    Gson gson = HttpTaskServer.getGson();

    protected BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public abstract void handle(HttpExchange exchange) throws IOException;

    protected void sendText(HttpExchange exchange, String response, int rCode) throws IOException {
        String message = "Запрос успешно обработан!\n" + response;
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(rCode, 0);
            os.write(message.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String response, int rCode) throws IOException {
        String message = "Произошла ошибка!\n" + response;
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(rCode, 0);
            os.write(message.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    protected void sendInteractions(HttpExchange exchange) throws IOException {
        String response = "Задача пересекается с существующими по времени выполнения!";
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
        exchange.close();
    }

    protected Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }
}
