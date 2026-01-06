package at.technikum.application.controllers;

import at.technikum.application.exceptions.BadRequestException;
import at.technikum.application.exceptions.DatabaseException;
import at.technikum.application.exceptions.ExceptionMapper;
import at.technikum.application.exceptions.UnauthorizedException;
import at.technikum.application.models.Media;
import at.technikum.application.models.User;
import at.technikum.application.services.UserService;
import at.technikum.application.util.MediaUtil;
import at.technikum.application.util.UserUtil;
import at.technikum.server.Request;
import at.technikum.server.Response;
import at.technikum.server.Status;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

public class UserController extends Controller {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public Response getUserProfile(Request request){
        try{
            int userID = Integer.parseInt(request.getPathParam("userId"));

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
            int userID = Integer.parseInt(request.getPathParam("userId"));

            if (request.getBody() == null || request.getBody().isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Request body is missing"));
            }

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
        try {
            int userID = Integer.parseInt(request.getPathParam("userId"));

            User principal = request.getCurrentUser();
            if (principal == null || principal.getUserID() != userID) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            String type = request.getQueryParams().get("type");

            if (type == null || type.isEmpty()) {
                return ExceptionMapper.toResponse(new BadRequestException("Recommendation type is missing"));
            }

            if (!type.equals("genre") && !type.equals("content")) {
                return ExceptionMapper.toResponse(new BadRequestException("Recommendation type must be 'genre' or 'content'"));
            }

            List<Media> recommendations = userService.getRecommendations(userID, type);

            JsonObject response = new JsonObject();
            response.addProperty("message", "Recommended media");
            response.addProperty("type", type);

            JsonArray mediaArray = new JsonArray();
            for (Media media : recommendations) {
                mediaArray.add(MediaUtil.mediaToJson(media));
            }
            response.add("recommendations", mediaArray);

            return json(Status.OK, response.toString());
        }catch (Exception exception) {
            return ExceptionMapper.toResponse(new DatabaseException("Failed get recommendations: " + exception.getMessage()));
        }
    }

    public Response getLeaderboard(Request request){
        try{
            User principal = request.getCurrentUser();
            if (principal == null ) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid user credentials"));
            }

            List<User> leaderboard = userService.getLeaderboard();

            JsonObject response = new JsonObject();
            response.addProperty("message", "Leaderboard list");
            JsonArray leaderboardArray = new JsonArray();

            for (User user : leaderboard){
                leaderboardArray.add(UserUtil.turnUserToJson(user));
            }

            response.add("leaderboard", leaderboardArray);

            return json(Status.OK, response.toString());
        } catch (Exception exception) {
            return ExceptionMapper.toResponse(new DatabaseException("Failed get user profile: " + exception.getMessage()));
        }
    }
}
