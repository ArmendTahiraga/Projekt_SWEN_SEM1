package at.technikum.application.controllers;

import at.technikum.server.ContentType;
import at.technikum.server.Response;
import at.technikum.server.Status;

public abstract class Controller {
    protected Response ok() {
        return status(Status.OK);
    }

    protected Response status(Status status) {
        return text(status.getMessage(), status);
    }

    protected Response text(String text, Status status) {
        return response(status, ContentType.TEXT_PLAIN, text);
    }

    protected Response error(Status status, String message) {
        return text("Error: " + (message == null || message.isEmpty() ? status.getMessage() : message), status);
    }

    protected Response json(Status status, String data) {
        return response(status, ContentType.APPLICATION_JSON, data);
    }

    private Response response(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatus(status);
        response.setContentType(contentType);
        response.setBody(body);

        return response;
    }
}
