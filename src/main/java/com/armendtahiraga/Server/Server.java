package com.armendtahiraga.Server;

import com.armendtahiraga.App.Application;
import com.armendtahiraga.Server.util.RequestMapper;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    private HttpServer httpServer;
    private final int port;
    private final Application application;

    public Server(int port, Application application) {
        this.port = port;
        this.application = application;
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress("localhost", port), 0);
            httpServer.createContext("/", new Handler(application, new RequestMapper()));
            httpServer.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
