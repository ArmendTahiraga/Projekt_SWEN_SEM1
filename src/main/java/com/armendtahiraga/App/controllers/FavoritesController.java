package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.Media;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.FavoritesService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class FavoritesController extends Controller {
    FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    public Response getUserFavorites(Request request){
        try{
            int userID = Integer.parseInt(request.getPath().split("/users/")[1].split("/favorites")[0]);

            User principal = request.getCurrentUser();
            if (principal == null && principal.getUserID() != userID) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            List<Media> favoriteMedias = favoritesService.getUserFavorites(userID);

            JsonObject response = new JsonObject();
            response.addProperty("message", "Favorites list");

            JsonArray favoritesMediaArray = new JsonArray();
            for (Media media : favoriteMedias) {
                favoritesMediaArray.add(mediaToJson(media));
            }

            response.add("favorites", favoritesMediaArray);

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to get the favorites list: " + exception.getMessage())));
        }
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

    private JsonObject mediaToJson(Media media) {
        JsonObject json = new JsonObject();
        json.addProperty("mediaID", media.getMediaID());
        json.addProperty("creatorUserId", media.getCreatorUserId());
        json.addProperty("title", media.getTitle());
        json.addProperty("description", media.getDescription());
        json.addProperty("mediaType", media.getMediaType());
        json.addProperty("releaseYear", media.getReleaseYear());
        json.addProperty("ageRestriction", media.getAgeRestriction());

        if (media.getGenres() != null) {
            JsonArray genresArray = new JsonArray();
            for (String genre : media.getGenres()) {
                genresArray.add(genre);
            }
            json.add("genres", genresArray);
        } else {
            json.add("genres", new JsonArray());
        }

        return json;
    }
}
