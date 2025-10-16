package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.services.RatingService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

public class RatingController extends Controller {
    private RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    public Response rateMedia(Request request){
        return ok();
    }

    public Response likeRating(Request request){
        return ok();
    }

    public Response updateRating(Request request){
        return ok();
    }

    public Response deleteRating(Request request){
        return ok();
    }

    public Response confirmRating(Request request){
        return ok();
    }
}
