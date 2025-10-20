package com.armendtahiraga.App;

import com.armendtahiraga.App.controllers.AuthController;
import com.armendtahiraga.App.controllers.MediaController;
import com.armendtahiraga.App.controllers.RatingController;
import com.armendtahiraga.App.controllers.UserController;
import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.exceptions.ExceptionMapper;
import com.armendtahiraga.App.exceptions.JsonConversionException;
import com.armendtahiraga.App.exceptions.NotFoundException;
import com.armendtahiraga.App.exceptions.NotJsonBodyException;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.repository.MediaRepository;
import com.armendtahiraga.App.repository.RatingRepository;
import com.armendtahiraga.App.repository.UserRepository;
import com.armendtahiraga.App.services.AuthService;
import com.armendtahiraga.App.services.MediaService;
import com.armendtahiraga.App.services.RatingService;
import com.armendtahiraga.App.services.UserService;
import com.armendtahiraga.Server.ContentType;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

import java.rmi.ServerError;

public class MRPApplication implements Application {
    private Router router;
    private ExceptionMapper exceptionMapper;

    private UserRepository userRepository;
    private MediaRepository mediaRepository;
    private RatingRepository ratingRepository;

    private AuthService authService;
    private UserService userService;
    private MediaService mediaService;
    private RatingService ratingService;

    private AuthController authController;
    private UserController userController;
    private MediaController mediaController;
    private RatingController ratingController;

    private static User loggedInUser;

    public MRPApplication(){
        Database.connect();
        this.router = new Router();
        this.exceptionMapper = new ExceptionMapper();

        this.userRepository = new UserRepository();
        this.mediaRepository = new MediaRepository();
        this.ratingRepository = new RatingRepository();

        this.authService = new AuthService(userRepository);
        this.userService = new UserService(userRepository);
        this.mediaService = new MediaService(mediaRepository);
        this.ratingService = new RatingService(ratingRepository);

        this.authController = new AuthController(authService);
        this.userController = new UserController(userService);
        this.mediaController = new MediaController(mediaService);
        this.ratingController = new RatingController(ratingService);

        createRoutes();
    }

    private void createRoutes(){
        router.addRoute("POST", "/api/users/register", authController::register);
        router.addRoute("POST", "/api/users/login", authController::login);

        router.addRoute("GET", "/api/users/{userId}/profile", userController::getUserProfile);
        router.addRoute("PUT", "/api/users/{userId}/profile", userController::updateUserProfile);
        router.addRoute("GET", "/api/users/{userId}/ratings", userController::getUserRatings);
        router.addRoute("GET", "/api/users/{userId}/favorites", userController::getUserFavorites);
        router.addRoute("POST", "/api/media/{mediaId}/favorite", userController::addMediaToFavorites);
        router.addRoute("DELETE", "/api/media/{mediaId}/favorite", userController::removeMediaFromFavorites);
        router.addRoute("GET", "/api/users/{userId}/recommendations", userController::getRecommendations);
        router.addRoute("GET", "/api/leaderboard", userController::getLeaderboard);

        router.addRoute("GET", "/api/media", mediaController::getAllMedia);
        router.addRoute("POST", "/api/media", mediaController::createMedia);
        router.addRoute("DELETE", "/api/media/{mediaId}", mediaController::deleteMedia);
        router.addRoute("GET", "/api/media/{mediaId}", mediaController::getMediaById);
        router.addRoute("PUT", "/api/media/{mediaId}", mediaController::updateMedia);

        router.addRoute("POST", "/api/media/{mediaId}/rate", ratingController::rateMedia);
        router.addRoute("POST", "/api/ratings/{ratingId}/like", ratingController::likeRating);
        router.addRoute("PUT", "/api/ratings/{ratingId}", ratingController::updateRating);
        router.addRoute("DELETE", "/api/ratings/{ratingId}", ratingController::deleteRating);
        router.addRoute("POST", "/api/ratings/{ratingId}/confirm", ratingController::confirmRating);
    }

    private void registerExceptions(){
        exceptionMapper.registerException(NotFoundException.class, Status.NOT_FOUND);
        exceptionMapper.registerException(JsonConversionException.class, Status.BAD_REQUEST);
        exceptionMapper.registerException(NotJsonBodyException.class, Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response handleRequest(Request request) {
        return router.findRoute(request)
            .map(route -> route.getEndpoint().handle(request))
            .orElseGet(() -> {
                if (router.pathExists(request.getPath())) {
                    return new Response(Status.METHOD_NOT_ALLOWED, ContentType.TEXT_PLAIN, Status.METHOD_NOT_ALLOWED.getMessage());
                }

                return new Response(Status.NOT_FOUND, ContentType.TEXT_PLAIN, Status.NOT_FOUND.getMessage());
            });
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedInUser) {
        MRPApplication.loggedInUser = loggedInUser;
    }
}
