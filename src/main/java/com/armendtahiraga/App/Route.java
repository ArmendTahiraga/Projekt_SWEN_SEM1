package com.armendtahiraga.App;


public class Route {
    private final String method;
    private final String path;
    private final Endpoint endpoint;

    public Route(String method, String path, Endpoint endpoint) {
        this.method = method;
        this.path = path;
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }
}
