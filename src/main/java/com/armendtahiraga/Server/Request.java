package com.armendtahiraga.Server;


import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private String body;
    private Map<String, String> pathParams = new HashMap<>();

    public Request(){}

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public String getPathParam(String key) {
        return pathParams.get(key);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void putPathParam(String key, String value) {
        pathParams.put(key, value);
    }
}
