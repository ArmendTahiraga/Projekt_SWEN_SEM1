package at.technikum;

import at.technikum.application.MRPApplication;
import at.technikum.server.Server;

public class Main {
    public static void main(String[] args) {
        MRPApplication app = new MRPApplication();
        Server server = new Server(8080, app);
        server.start();
    }
}