package at.technikum.application;

import at.technikum.server.Request;

import java.util.*;

public class Router {
    private List<Route> routes;

    public Router() {
        this.routes = new ArrayList<>();
    }

    public Optional<Route> findRoute(Request request) {
        String method = request.getMethod() == null ? "" : request.getMethod().toUpperCase();
        String path   = request.getPath() == null ? "" : request.getPath();

        for (Route route : routes) {
            if (!route.getMethod().equalsIgnoreCase(method)){
                continue;
            }

            Map<String, String> params = matchAndExtract(route.getPath(), path);
            if (params != null) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    request.putPathParam(entry.getKey(), entry.getValue());
                }

                return Optional.of(route);
            }
        }
        return Optional.empty();
    }

    public boolean pathExists(String path) {
        return routes.stream().anyMatch(route -> route.getPath().equals(path == null ? "" : path));
    }

    public void addRoute(String method, String path, Endpoint endpoint) {
        routes.add(new Route(method, path, endpoint));
    }

    private static Map<String, String> matchAndExtract(String template, String path) {
        template = normalize(template);
        path = normalize(path);

        String[] t = template.split("/");
        String[] p = path.split("/");

        if (t.length != p.length) {
            return null;
        }

        Map<String, String> params = new HashMap<>();
        for (int i = 0; i < t.length; i++) {
            String ts = t[i];
            String ps = p[i];

            if (ts.startsWith("{") && ts.endsWith("}")) {
                String name = ts.substring(1, ts.length() - 1);
                params.put(name, ps);
            } else if (!ts.equals(ps)) {
                return null;
            }
        }

        return params;
    }

    private static String normalize(String s) {
        if (s == null || s.isEmpty()){
            return "";
        }
        s = s.replaceAll("/{2,}", "/");

        if (s.startsWith("/")) {
            s = s.substring(1);
        }

        if (s.endsWith("/"))  {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
