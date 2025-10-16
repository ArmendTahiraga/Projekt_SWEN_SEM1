package com.armendtahiraga.App.controllers;

import com.armendtahiraga.App.services.MediaService;
import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

public class MediaController extends Controller {
    private MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    public Response getAllMedia(Request request){
        return ok();
    }

    public Response createMedia(Request request){
        return ok();
    }

    public Response deleteMedia(Request request){
        return ok();
    }

    public Response getMediaById(Request request){
        return ok();
    }

    public Response updateMedia(Request request){
        return ok();
    }
}
