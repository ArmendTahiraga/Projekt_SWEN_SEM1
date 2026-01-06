package at.technikum.application;

import at.technikum.server.Request;
import at.technikum.server.Response;

public interface Application {
    Response handleRequest(Request request);
}
