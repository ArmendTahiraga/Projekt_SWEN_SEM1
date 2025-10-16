package com.armendtahiraga.App;

import com.armendtahiraga.App.controllers.AuthController;
import com.armendtahiraga.Server.ContentType;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

public class MRPApplication implements Application {
    private Router router;
    private AuthController authController;

    public MRPApplication(){
        this.router = new Router();
        this.authController = new AuthController();

        createRoutes();
    }

    private void createRoutes(){
        router.addRoute("POST", "/users/register", authController::register);
        router.addRoute("POST", "/users/login", authController::login);
    }

    @Override
    public Response handleRequest(Request request) {
        return router.findRoute(request)
            .map(route -> route.getEndpoint().handle(request))
            .orElseGet(() -> {
                if (router.pathExists(request.getPath())) {
                    return new Response(Status.METHOD_NOT_ALLOWED, ContentType.TEXT_PLAIN, Status.METHOD_NOT_ALLOWED.getMessage());
                }

                return new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, Status.NOT_FOUND.getMessage());
            });
    }
}
