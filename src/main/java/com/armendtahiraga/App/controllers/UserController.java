package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.UserService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonObject;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response getUserProfile(Request request){
        try{
            String path = request.getPath().split("/users/")[1];
            int userID = Integer.parseInt(path.split("/profile")[0]);
            System.out.println(userID);
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
            return error(Status.INTERNAL_SERVER_ERROR, exception.getMessage());
        }
    }

    public Response updateUserProfile(Request request){
        return ok();
    }

    public Response getUserRatings(Request request){
        return ok();
    }

    public Response getUserFavorites(Request request){
        return ok();
    }

    public Response addMediaToFavorites(Request request){
        return ok();
    }

    public Response removeMediaFromFavorites(Request request){
        return ok();
    }

    public Response getRecommendations(Request request){
        return ok();
    }

    public Response getLeaderboard(Request request){
        return ok();
    }
}
