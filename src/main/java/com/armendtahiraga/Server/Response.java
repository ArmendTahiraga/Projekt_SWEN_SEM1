package com.armendtahiraga.Server;

public class Response {
    private Status status;
    private String body;
    private ContentType contentType;

    public Response(){}

    public Response(Status status, ContentType contentType, String body) {
        this.status = status;
        this.body = body;
        this.contentType = contentType;
    }

    public Status getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return status.getMessage();
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType.getMimeType();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
