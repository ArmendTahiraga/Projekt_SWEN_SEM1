package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.DatabaseException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.AuthService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class AuthController extends Controller{
    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Response register(Request request) {
        try{
            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is empty"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String username = body.get("username").getAsString();
            String email = body.get("email").getAsString();
            String password = body.get("password").getAsString();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Username or email or password is missing"));
            }

            User createdUser = authService.register(username, email, password);

            JsonObject response = new JsonObject();
            response.addProperty("id", createdUser.getUserID());
            response.addProperty("username", createdUser.getUsername());
            response.addProperty("token", createdUser.getToken());
            response.addProperty("message", "User registered");

            return json(Status.CREATED, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new DatabaseException("Failed to register user: " + exception.getMessage()));
        }
    }

    public Response login(Request request) {
        try{
            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is empty"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String username = body.get("username").getAsString();
            String password = body.get("password").getAsString();

            if (username.isEmpty() || password.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Username or password is missing"));
            }

            User user = authService.login(username, password);

            JsonObject response = new JsonObject();
            response.addProperty("id", user.getUserID());
            response.addProperty("username", user.getUsername());
            response.addProperty("token", user.getToken());
            response.addProperty("message", "Login successful");

            return json(Status.OK, response.toString());
        } catch (Exception exception) {
            return ExceptionMapper.toResponse(new DatabaseException("Failed to login user: " + exception.getMessage()));
        }
    }
}
