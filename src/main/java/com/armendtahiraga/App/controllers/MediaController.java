package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.Media;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.MediaService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MediaController extends Controller {
    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public Response getMedias(Request request){
        try{
            User principal = request.getCurrentUser();
            if (principal == null && Objects.equals(principal.getUsername(), request.getCurrentUser().getUsername())) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            List<Media> medias = mediaService.getMedias();

            JsonObject response = new JsonObject();
            response.addProperty("message", "Medias fetched successfully");

            JsonArray mediaArray = new JsonArray();
            for (Media media : medias) {
                mediaArray.add(mediaToJson(media));
            }

            response.add("medias", mediaArray);

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to get all media: " + exception.getMessage()));
        }
    }

    public Response createMedia(Request request){
        try{
            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

            User principal = request.getCurrentUser();
            if (principal == null && Objects.equals(principal.getUsername(), request.getCurrentUser().getUsername())) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String title = body.get("title").getAsString();
            String description = body.get("description").getAsString();
            String mediaType = body.get("mediaType").getAsString();
            int releaseYear = body.get("releaseYear").getAsInt();
            List<JsonElement> genreList = body.get("genres").getAsJsonArray().asList();
            List<String> genres = new ArrayList<>();
            for (JsonElement genre : genreList) {
                genres.add(genre.getAsString());
            }
            int ageRestriction = body.get("ageRestriction").getAsInt();

            if (title.isEmpty() || description.isEmpty() || mediaType.isEmpty() || genres.isEmpty() || releaseYear <= 0 || ageRestriction < 0) {
                return ExceptionMapper.toResponse(new BadRequestException("Missing required media fields"));
            }

            Media createdMedia = mediaService.createMedia(principal.getUserID(), title, description, mediaType, releaseYear, ageRestriction, genres);

            JsonObject response = mediaToJson(createdMedia);
            response.addProperty("message", "Media created successfully");

            return json(Status.CREATED, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to create media: " + exception.getMessage()));
        }
    }

    public Response deleteMedia(Request request){
        return ok();
    }

    public Response getMediaById(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPath().split("/media/")[1]);

            User principal = request.getCurrentUser();
            if (principal == null && Objects.equals(principal.getUsername(), request.getCurrentUser().getUsername())) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            Media media = mediaService.getMediaById(mediaID);

            JsonObject response = mediaToJson(media);
            response.addProperty("message", "Medias fetched successfully");


            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to get all media: " + exception.getMessage()));
        }
    }

    public Response updateMedia(Request request){
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
