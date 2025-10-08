package com.armendtahiraga.Server;

public class Response {
    private Status statusCode;
    private String body;
    private ContentType contentType;

    public Response(){}

    public Response(Status statusCode, ContentType contentType, String body) {
        this.statusCode = statusCode;
        this.body = body;
        this.contentType = contentType;
    }

    public Status getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusCode.getMessage();
    }

    public String getBody() {
        return body;
    }

    public String getContentType() {
        return contentType.getMimeType();
    }

    public void setStatusCode(Status statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
}
