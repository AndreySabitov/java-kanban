package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import services.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange);
        switch (endpoint) {
            case GET_PRIORITIZED_TASKS:
                handleGetPrioritizedTasks(exchange);
                break;
            case UNKNOWN:
                sendNotFound(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getPrioritizedTasks()), 200);
    }

    private Endpoint getEndpoint(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");
        if (method.equals("GET") && parts.length == 2) {
            return Endpoint.GET_PRIORITIZED_TASKS;
        } else {
            return Endpoint.UNKNOWN;
        }
    }
}
