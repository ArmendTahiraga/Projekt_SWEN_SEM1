package com.armendtahiraga.Server.util;

import com.armendtahiraga.Server.Request;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RequestMapper {

    public Request fromExchange(HttpExchange exchange) throws IOException {
        Request request = new Request();
        request.setMethod(exchange.getRequestMethod());
        request.setPath(exchange.getRequestURI().getPath());

        InputStream is = exchange.getRequestBody();

        if (is == null) {
            return request;
        }

        byte[] buf = is.readAllBytes();
        request.setBody(new String(buf, StandardCharsets.UTF_8));

        return request;
    }
}
