package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import services.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
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
        int rCode = 200;
        String response;
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        response = gson.toJson(prioritizedTasks);
        sendText(exchange, response, rCode);
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
