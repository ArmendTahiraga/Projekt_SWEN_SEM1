package com.armendtahiraga.App;

import com.armendtahiraga.App.controllers.*;
import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.exceptions.*;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.repository.FavoritesRepository;
import com.armendtahiraga.App.repository.MediaRepository;
import com.armendtahiraga.App.repository.RatingRepository;
import com.armendtahiraga.App.repository.UserRepository;
import com.armendtahiraga.App.services.*;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

public class MRPApplication implements Application {
    private Router router;

    private UserRepository userRepository;
    private MediaRepository mediaRepository;
    private RatingRepository ratingRepository;
    private FavoritesRepository favoritesRepository;

    private AuthService authService;
    private UserService userService;
    private MediaService mediaService;
    private RatingService ratingService;
    private FavoritesService favoritesService;

    private AuthController authController;
    private UserController userController;
    private MediaController mediaController;
    private RatingController ratingController;
    private FavoritesController favoritesController;

    public MRPApplication(){
        Database.connect();
        this.router = new Router();

        this.userRepository = new UserRepository();
        this.mediaRepository = new MediaRepository();
        this.ratingRepository = new RatingRepository();
        this.favoritesRepository = new FavoritesRepository();

        this.authService = new AuthService(userRepository);
        this.userService = new UserService(userRepository);
        this.mediaService = new MediaService(mediaRepository);
        this.ratingService = new RatingService(ratingRepository);
        this.favoritesService = new FavoritesService(favoritesRepository);

        this.authController = new AuthController(authService);
        this.userController = new UserController(userService);
        this.mediaController = new MediaController(mediaService);
        this.ratingController = new RatingController(ratingService);
        this.favoritesController = new FavoritesController(favoritesService);

        createRoutes();
        registerExceptions();
    }

    private void createRoutes(){
        router.addRoute("POST", "/api/users/register", authController::register);
        router.addRoute("POST", "/api/users/login", authController::login);

        router.addRoute("GET", "/api/users/{userId}/profile", userController::getUserProfile);
        router.addRoute("PUT", "/api/users/{userId}/profile", userController::updateUserProfile);
        router.addRoute("GET", "/api/users/{userId}/recommendations", userController::getRecommendations);
        router.addRoute("GET", "/api/leaderboard", userController::getLeaderboard);

        router.addRoute("GET", "/api/media", mediaController::getMedias);
        router.addRoute("POST", "/api/media", mediaController::createMedia);
        router.addRoute("DELETE", "/api/media/{mediaId}", mediaController::deleteMedia);
        router.addRoute("GET", "/api/media/{mediaId}", mediaController::getMediaById);
        router.addRoute("PUT", "/api/media/{mediaId}", mediaController::updateMedia);

        router.addRoute("POST", "/api/media/{mediaId}/rate", ratingController::rateMedia);
        router.addRoute("POST", "/api/ratings/{ratingId}/like", ratingController::likeRating);
        router.addRoute("PUT", "/api/ratings/{ratingId}", ratingController::updateRating);
        router.addRoute("DELETE", "/api/ratings/{ratingId}", ratingController::deleteRating);
        router.addRoute("POST", "/api/ratings/{ratingId}/confirm", ratingController::confirmRatingComment);
        router.addRoute("GET", "/api/users/{userId}/ratings", ratingController::getUserRatings);

        router.addRoute("GET", "/api/users/{userId}/favorites", favoritesController::getUserFavorites);
        router.addRoute("POST", "/api/media/{mediaId}/favorite", favoritesController::addMediaToFavorites);
        router.addRoute("DELETE", "/api/media/{mediaId}/favorite", favoritesController::removeMediaFromFavorites);
    }

    private void registerExceptions(){
        ExceptionMapper.registerException(NotFoundException.class, Status.NOT_FOUND);
        ExceptionMapper.registerException(JsonConversionException.class, Status.BAD_REQUEST);
        ExceptionMapper.registerException(NotJsonBodyException.class, Status.INTERNAL_SERVER_ERROR);
        ExceptionMapper.registerException(BadRequestException.class, Status.BAD_REQUEST);
        ExceptionMapper.registerException(UnauthorizedException.class, Status.UNAUTHORIZED);
        ExceptionMapper.registerException(ForbiddenException.class, Status.FORBIDDEN);
        ExceptionMapper.registerException(InvalidParameterException.class, Status.BAD_REQUEST);
        ExceptionMapper.registerException(DatabaseException.class, Status.INTERNAL_SERVER_ERROR);
        ExceptionMapper.registerException(InvalidTokenException.class, Status.UNAUTHORIZED);
        ExceptionMapper.registerException(MethodNotAllowedException.class, Status.METHOD_NOT_ALLOWED);
    }

    private static boolean isAuthNotRequired(Request req) {
        String path = req.getPath();
        String method = req.getMethod();

        if (method.equalsIgnoreCase("POST")) {
            return "/api/users/login".equals(path) || "/api/users/register".equals(path);
        }

        return false;
    }

    private static String extractToken(Request req) {
        String header = req.getHeader("Authorization");

        if (header == null) {
            return null;
        }

        String[] parts = header.split("\\s+");
        if (parts.length == 2 && parts[0].equalsIgnoreCase("Bearer")) {
            return parts[1].trim();
        }

        return header.trim();
    }

    @Override
    public Response handleRequest(Request request) {
        if (!isAuthNotRequired(request)) {
            String token = extractToken(request);

            if (token == null || token.isEmpty()) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Authorization token is missing"));
            }

            try {
                User user = authService.verifyToken(token);
                request.setCurrentUser(user);
            } catch (IllegalArgumentException exception) {
                return ExceptionMapper.toResponse(new UnauthorizedException("Invalid token"));
            }
        }

        return router.findRoute(request)
            .map(route -> route.getEndpoint().handle(request))
            .orElseGet(() -> {
                if (router.pathExists(request.getPath())) {
                    return ExceptionMapper.toResponse(new MethodNotAllowedException("Method not allowed"));
                }

                return ExceptionMapper.toResponse(new NotFoundException("Not found"));
            });
    }
}
