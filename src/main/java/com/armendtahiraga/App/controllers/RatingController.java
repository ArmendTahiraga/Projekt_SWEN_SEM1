package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.Rating;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.RatingService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

public class RatingController extends Controller {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response rateMedia(Request request){
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

            int stars = body.get("stars").getAsInt();
            String comment = body.get("comment").getAsString();

            if ((stars < 0 || stars > 5) && comment.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Invalid rating data"));
            }

            boolean success = ratingService.rateMedia(principal.getUserID(), mediaID, stars, comment);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating submitted");
                return json(Status.CREATED, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to rate media"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to rate media: " + exception.getMessage())));
        }
    }

    public Response likeRating(Request request){
        try{
            int ratingID = Integer.parseInt(request.getPathParam("ratingId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = ratingService.likeRating(ratingID);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating liked");
                return json(Status.OK, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to rate media"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to rate media: " + exception.getMessage())));
        }
    }

    public Response updateRating(Request request){
        try{
            int ratingID = Integer.parseInt(request.getPathParam("ratingId"));

            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            JsonObject body = JsonParser.parseString(request.getBody()).getAsJsonObject();

            int stars = body.get("stars").getAsInt();
            String comment = body.get("comment").getAsString();

            if ((stars < 0 || stars > 5) && comment.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Invalid rating data"));
            }

            boolean success = ratingService.updateRating(ratingID, principal.getUserID(), stars, comment);

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating updated");
                return json(Status.OK, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to update media rating"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to update media rating: " + exception.getMessage())));
        }
    }

    public Response deleteRating(Request request){
        try{
            int ratingID = Integer.parseInt(request.getPathParam("ratingId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = ratingService.deleteRating(ratingID, principal.getUserID());

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating deleted");
                return json(Status.DELETED, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to delete media rating"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to delete media rating: " + exception.getMessage())));
        }
    }

    public Response confirmRatingComment(Request request){
        try{
            int ratingID = Integer.parseInt(request.getPathParam("ratingId"));

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = ratingService.confirmRatingComment(ratingID, principal.getUserID());

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating comment confirmed");
                return json(Status.OK, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to confirm rating comment"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to confirm rating comment: " + exception.getMessage())));
        }
    }

    public Response getUserRatings(Request request){
        try{
            int userID = Integer.parseInt(request.getPathParam("userId"));

            User principal = request.getCurrentUser();
            if (principal == null || principal.getUserID() != userID) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            List<Rating> ratings = ratingService.getUserRatings(userID);

            JsonObject response = new JsonObject();
            response.addProperty("message", "Rating history");

            JsonArray ratingArray = new JsonArray();
            for (Rating rating : ratings) {
                ratingArray.add(ratingToJson(rating));
            }
            response.add("ratings", ratingArray);

            return json(Status.OK, response.toString());
        } catch (Exception exception){
            return ExceptionMapper.toResponse(new BadRequestException("Failed to get user rating history: " + exception.getMessage()));
        }
    }

    private JsonObject ratingToJson(Rating rating){
        JsonObject json = new JsonObject();
        json.addProperty("ratingID", rating.getRatingID());
        json.addProperty("mediaID", rating.getMediaID());
        json.addProperty("userID", rating.getUserID());
        json.addProperty("stars", rating.getStars());
        json.addProperty("comment", rating.getComment());
        json.addProperty("timestamp", rating.getTimestamp());
        json.addProperty("confirmed", rating.isConfirmed());
        json.addProperty("likes", rating.getLikes());

        return json;
    }
}
