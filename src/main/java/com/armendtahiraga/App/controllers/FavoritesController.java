package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.services.FavoritesService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

public class FavoritesController extends Controller {
    FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    public Response getUserFavorites(Request request){
        return ok();
    }

    public Response addMediaToFavorites(Request request){
        return ok();
    }

    public Response removeMediaFromFavorites(Request request){
        return ok();
    }
}
