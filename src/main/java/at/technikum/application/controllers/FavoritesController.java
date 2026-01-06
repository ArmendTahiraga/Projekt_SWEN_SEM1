package at.technikum.application.controllers;

import at.technikum.application.exceptions.BadRequestException;
import at.technikum.application.exceptions.ExceptionMapper;
import at.technikum.application.exceptions.NotFoundException;
import at.technikum.application.exceptions.UnauthorizedException;
import at.technikum.application.models.Media;
import at.technikum.application.models.User;
import at.technikum.application.services.FavoritesService;
import at.technikum.server.Request;
import at.technikum.server.Response;
import at.technikum.server.Status;
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
            int userID = Integer.parseInt(request.getPathParam("userId"));

            User principal = request.getCurrentUser();
            if (principal == null || principal.getUserID() != userID) {
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
            int mediaID = Integer.parseInt(request.getPathParam("mediaId"));

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
                return ExceptionMapper.toResponse(new NotFoundException("Failed to mark media as favorite"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new NotFoundException("Failed to mark media as favorite: " + exception.getMessage())));
        }
    }

    public Response removeMediaFromFavorites(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPathParam("mediaId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = favoritesService.removeMediaFromFavorites(principal.getUserID(), mediaID);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Unmarked from favorites");
                return json(Status.DELETED, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to unmark media from favorites"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to unmark media from favorites: " + exception.getMessage())));
        }
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
