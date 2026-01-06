package at.technikum.application;

import at.technikum.server.Request;
import at.technikum.server.Response;

@FunctionalInterface
public interface Endpoint {
    Response handle(Request request);
}
