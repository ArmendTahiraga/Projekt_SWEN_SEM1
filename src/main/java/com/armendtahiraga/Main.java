package com.armendtahiraga;

import com.armendtahiraga.App.MRPApplication;
import com.armendtahiraga.Server.Server;

public class Main {
    public static void main(String[] args) {
        MRPApplication app = new MRPApplication();
        Server server = new Server(8080, app);
        server.start();
    }
}