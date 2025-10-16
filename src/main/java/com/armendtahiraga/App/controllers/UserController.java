package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.services.UserService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response getUserProfile(Request request){
        return ok();
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
