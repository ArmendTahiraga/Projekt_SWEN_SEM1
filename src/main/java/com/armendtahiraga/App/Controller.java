package com.armendtahiraga.App;

import com.armendtahiraga.Server.ContentType;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

public abstract class Controller {
    //Bekommt user daten von request und erstellt user objekt
    //User modifizieren und löschen

    public abstract Response handle(Request request);

    protected Response ok() {
        return status(Status.OK);
    }

    protected Response status(Status status) {
        return text(status.getMessage(), status);
    }

    protected Response text(String text) {
        return text(text, Status.OK);
    }

    protected Response text(String text, Status status) {
        return response(status, ContentType.TEXT_PLAIN, text);
    }

    private Response response(Status status, ContentType contentType, String body) {
        Response response = new Response();
        response.setStatusCode(status);
        response.setContentType(contentType);
        response.setBody(body);

        return response;
    }
}
