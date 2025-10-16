package com.armendtahiraga.App;

import com.armendtahiraga.Server.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Router {
    private List<Route> routes;

    public Router() {
        this.routes = new ArrayList<>();
    }

    public Optional<Route> findRoute(Request request) {
        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase();
        String path = request.getPath() == null ? "" : request.getPath();

        return routes.stream()
            .filter(route -> route.getMethod().equals(method) &&
                                    route.getPath().equals(path))
            .findFirst();
    }

    public boolean pathExists(String path) {
        return routes.stream().anyMatch(route -> route.getPath().equals(path == null ? "" : path));
    }

    public void addRoute(String method, String path, Endpoint endpoint) {
        routes.add(new Route(method, path, endpoint));
    }
}
