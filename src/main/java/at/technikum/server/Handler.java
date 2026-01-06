package at.technikum.server;

import at.technikum.application.Application;
import at.technikum.server.util.RequestMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class Handler implements HttpHandler {
    private final Application application;
    private final RequestMapper requestMapper;

    public Handler(Application application, RequestMapper requestMapper) {
        this.application = application;
        this.requestMapper = requestMapper;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Request request = requestMapper.fromExchange(exchange);
        Response response = application.handleRequest(request);
        send(exchange, response);
    }

    public void send(HttpExchange exchange, Response response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", response.getContentType());
        byte[] bytes = response.getBody().getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(response.getStatus().getCode(), bytes.length);
        try(OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(bytes);
        }
    }
}
