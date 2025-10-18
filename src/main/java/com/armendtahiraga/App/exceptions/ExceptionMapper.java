package com.armendtahiraga.App.exceptions;

import com.armendtahiraga.Server.ContentType;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

import java.util.HashMap;
import java.util.Map;

public class ExceptionMapper {
    private final Map<Class<?>, Status> exceptionMap;

    public ExceptionMapper() {
        this.exceptionMap = new HashMap<>();
    }

    public void registerException(Class<?> exceptionClass, Status status) {
        exceptionMap.put(exceptionClass, status);
    }

    public Response ToResponse(Exception exception) {
        Response response = new Response();
        Status status = exceptionMap.get(exception.getClass());

        response.setStatus(status != null ? status : Status.INTERNAL_SERVER_ERROR);
        response.setContentType(ContentType.TEXT_PLAIN);
        response.setBody(exception.getMessage());

        return response;
    }
}
