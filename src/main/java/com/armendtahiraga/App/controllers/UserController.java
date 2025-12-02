package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.DatabaseException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.UserService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response getUserProfile(Request request){
        try{
            int userID = Integer.parseInt(request.getPath().split("/users/")[1].split("/profile")[0]);

            User principal = request.getCurrentUser();
            if (principal == null || principal.getUserID() != userID) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            User user = userService.getUserByID(userID);

            JsonObject response = new JsonObject();
            response.addProperty("id", user.getUserID());
            response.addProperty("username", user.getUsername());
            response.addProperty("email", user.getEmail());
            response.addProperty("favoriteGenre", user.getFavoriteGenre());
            if (user.getFavoriteMedias() != null && !user.getFavoriteMedias().isEmpty()){
                response.addProperty("favoriteMedias", user.getFavoriteMedias().toString());
            } else {
                response.addProperty("favoriteMedias", "[]");
            }

            return json(Status.OK, response.toString());
        } catch (Exception exception) {
            return ExceptionMapper.toResponse(new DatabaseException("Failed get user profile: " + exception.getMessage()));
        }
    }

    public Response updateUserProfile(Request request){
        try{
            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

            int userID = Integer.parseInt(request.getPath().split("/users/")[1].split("/profile")[0]);

            User principal = request.getCurrentUser();
            if (principal == null || principal.getUserID() != userID) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String email = body.get("email").getAsString();
            String favoriteGenre = body.get("favoriteGenre").getAsString();

            if (favoriteGenre.isEmpty() || email.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("User email or favorite genre are missing"));
            }

            User updatedUser = userService.updateUser(userID, email, favoriteGenre);

            JsonObject response = new JsonObject();
            response.addProperty("id", updatedUser.getUserID());
            response.addProperty("username", updatedUser.getUsername());
            response.addProperty("message", "Profile updated successfully");

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new DatabaseException("Failed update user profile: " + exception.getMessage()));
        }
    }

    public Response getRecommendations(Request request){
        return ok();
    }

    public Response getLeaderboard(Request request){
        return ok();
    }
}
