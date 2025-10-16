package com.armendtahiraga.App.controllers;

import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;
import com.armendtahiraga.Server.Status;

public class AuthController extends Controller{

    public Response register(Request request) {

        return text("registered", Status.OK);
    }

    public Response login(Request request) {

        return text("logged in", Status.OK);
    }
}
