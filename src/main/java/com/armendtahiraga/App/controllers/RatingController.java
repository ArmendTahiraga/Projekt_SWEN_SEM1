package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.exceptions.BadRequestException;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.UnauthorizedException;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.services.RatingService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class RatingController extends Controller {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response rateMedia(Request request){
        try{
            int mediaID = Integer.parseInt(request.getPath().split("/media/")[1].split("/rate")[0]);

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
            int ratingID = Integer.parseInt(request.getPath().split("/ratings/")[1].split("/like")[0]);

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
            int ratingID = Integer.parseInt(request.getPath().split("/ratings/")[1]);

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
            int ratingID = Integer.parseInt(request.getPath().split("/ratings/")[1]);

            User principal = request.getCurrentUser();
            if (principal == null) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            boolean success = ratingService.deleteRating(ratingID, principal.getUserID());

            if (success) {
                JsonObject response = new JsonObject();
                response.addProperty("message", "Rating deleted");
                System.out.println("Rating deleted successfully");
                return json(Status.DELETED, response.toString());
            } else {
                return ExceptionMapper.toResponse(new BadRequestException("Failed to delete media rating"));
            }

        } catch (Exception exception){
            return ExceptionMapper.toResponse((new BadRequestException("Failed to delete media rating: " + exception.getMessage())));
        }
    }

    public Response confirmRating(Request request){
        return ok();
    }
}
