package at.technikum.application.controllers;

import at.technikum.application.exceptions.BadRequestException;
import at.technikum.application.exceptions.ExceptionMapper;
import at.technikum.application.exceptions.UnauthorizedException;
import at.technikum.application.models.Media;
import at.technikum.application.models.User;
import at.technikum.application.services.MediaService;
import at.technikum.application.util.MediaUtil;
import at.technikum.server.Request;
import at.technikum.server.Response;
import at.technikum.server.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaController extends Controller {
    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public Response getMedias(Request request){
        try{
            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            String title = request.getQueryParams().get("title") != null ? request.getQueryParams().get("title") : "";
            String genre = request.getQueryParams().get("genre") != null ? request.getQueryParams().get("genre") : "";
            String mediaType = request.getQueryParams().get("mediaType") != null ? request.getQueryParams().get("mediaType") : "";
            int releaseYear = request.getQueryParams().get("releaseYear") != null ? Integer.parseInt(request.getQueryParams().get("releaseYear")) : -1;
            int ageRestriction = request.getQueryParams().get("ageRestriction") != null ? Integer.parseInt(request.getQueryParams().get("ageRestriction")) : -1;
            int rating = request.getQueryParams().get("rating") != null ? Integer.parseInt(request.getQueryParams().get("rating")) : -1;
            String sortBy = request.getQueryParams().get("sortBy") != null ? request.getQueryParams().get("sortBy") : "none";

            if (!sortBy.equals("title") && !sortBy.equals("year") && !sortBy.equals("score") && !sortBy.equals("none")) {
                return ExceptionMapper.toResponse(new BadRequestException("Recommendation type must be 'genre' or 'content'"));
            }

            Map<String, ?> filters = Map.of(
                    "title", title,
                    "genre", genre,
                    "mediaType", mediaType,
                    "releaseYear", releaseYear,
                    "ageRestriction", ageRestriction,
                    "rating", rating
            );

            List<Media> medias = mediaService.getMedias(filters);

            JsonObject response = new JsonObject();
            response.addProperty("message", "Medias fetched successfully");

            JsonArray mediaArray = new JsonArray();
            for (Media media : medias) {
                mediaArray.add(MediaUtil.mediaToJson(media));
            }

            response.add("medias", mediaArray);

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to get medias: " + exception.getMessage()));
        }
    }

    public Response createMedia(Request request){
        try{
            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String title = body.get("title").getAsString();
            String description = body.get("description").getAsString();
            String mediaType = body.get("mediaType").getAsString();
            int releaseYear = body.get("releaseYear").getAsInt();
            int ageRestriction = body.get("ageRestriction").getAsInt();
            List<JsonElement> genreList = body.get("genres").getAsJsonArray().asList();
            List<String> genres = new ArrayList<>();
            for (JsonElement genre : genreList) {
                genres.add(genre.getAsString());
            }

            if (title.isEmpty() || description.isEmpty() || mediaType.isEmpty() || genres.isEmpty() || releaseYear <= 0 || ageRestriction < 0) {
                return ExceptionMapper.toResponse(new BadRequestException("Missing required media fields"));
            }

            Media createdMedia = mediaService.createMedia(principal.getUserID(), title, description, mediaType, releaseYear, ageRestriction, genres);

            JsonObject response = MediaUtil.mediaToJson(createdMedia);
            response.addProperty("message", "Media created successfully");

            return json(Status.CREATED, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to create media: " + exception.getMessage()));
        }
    }

    public Response deleteMedia(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPathParam("mediaId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            mediaService.deleteMedia(mediaID);

            JsonObject response = new JsonObject();
            response.addProperty("message", "Media deleted successfully");

            return json(Status.DELETED, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to delete media: " + exception.getMessage()));
        }
    }

    public Response getMediaById(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPathParam("mediaId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            Media media = mediaService.getMediaById(mediaID);

            JsonObject response = MediaUtil.mediaToJson(media);
            response.addProperty("message", "Media fetched successfully");

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to get media: " + exception.getMessage()));
        }
    }

    public Response updateMedia(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPathParam("mediaId"));

            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            String title = body.get("title").getAsString();
            String description = body.get("description").getAsString();
            String mediaType = body.get("mediaType").getAsString();
            int releaseYear = body.get("releaseYear").getAsInt();
            int ageRestriction = body.get("ageRestriction").getAsInt();
            List<JsonElement> genreList = body.get("genres").getAsJsonArray().asList();
            List<String> genres = new ArrayList<>();
            for (JsonElement genre : genreList) {
                genres.add(genre.getAsString());
            }

            if (title.isEmpty() || description.isEmpty() || mediaType.isEmpty() || genres.isEmpty() || releaseYear <= 0 || ageRestriction < 0) {
                return ExceptionMapper.toResponse(new BadRequestException("Missing required media fields"));
            }

            Media createdMedia = mediaService.updateMedia(mediaID, title, description, mediaType, releaseYear, ageRestriction, genres);

            JsonObject response = MediaUtil.mediaToJson(createdMedia);
            response.addProperty("message", "Media updated successfully");

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to update media: " + exception.getMessage()));
        }
    }
}
