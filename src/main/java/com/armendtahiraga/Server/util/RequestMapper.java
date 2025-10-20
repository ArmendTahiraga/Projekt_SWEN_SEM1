package com.armendtahiraga.Server.util;

import com.armendtahiraga.Server.Request;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());

        Headers headers = exchange.getRequestHeaders();
        for (String key : headers.keySet()) {
            String value = String.join(",", headers.get(key));
            request.putHeader(key, value);
        }

        InputStream inputStream = exchange.getRequestBody();

        if (inputStream == null) {
            return request;
        }

        byte[] body = inputStream.readAllBytes();
        request.setBody(new String(body, StandardCharsets.UTF_8));

        return request;
    }
}
