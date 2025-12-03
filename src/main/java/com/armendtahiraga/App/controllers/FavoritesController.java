package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.FavoritesService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonObject;

public class FavoritesController extends Controller {
    FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    public Response getUserFavorites(Request request){
        return ok();
    }

    public Response addMediaToFavorites(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPath().split("/media/")[1].split("/favorite")[0]);

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = favoritesService.addMediaToFavorites(principal.getUserID(), mediaID);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Marked as favorite");
                return json(Status.OK, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to mark media as favorite"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to mark media as favorite: " + exception.getMessage())));
        }
    }

    public Response removeMediaFromFavorites(Request request){
        return ok();
    }
}
