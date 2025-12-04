package com.armendtahiraga.Server;


import com.armendtahiraga.App.models.User;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private String method;
    private String path;
    private String body;
    private Map<String, String> pathParams   = new HashMap<>();
    private Map<String, String> headers      = new HashMap<>();
    private Map<String, String> queryParams  = new HashMap<>();   // <-- NEW
    private User currentUser;

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

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setPath(String rawPath) {
        if (rawPath == null) {
            this.path = "";
            queryParams.clear();
            return;
        }

        String[] parts = rawPath.split("\\?", 2);

        this.path = parts[0];

        if (parts.length == 2) {
            parseQueryParams(parts[1]);
        } else {
            queryParams.clear();
        }
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void putPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    public void putHeader(String name, String value) {
        headers.put(name, value);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private void parseQueryParams(String queryString) {
        queryParams.clear();

        if (queryString == null || queryString.isEmpty()) {
            return;
        }

        String[] params = queryString.split("&");

        for (String param : params) {
            if (param.isEmpty()) {
                continue;
            }

            String[] keyValuePair = param.split("=", 2);

            if (keyValuePair.length == 2) {
                queryParams.put(keyValuePair[0], keyValuePair[1]);
            } else if (keyValuePair.length == 1) {
                queryParams.put(keyValuePair[0], "");
            }
        }
    }
}
