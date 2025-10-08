package com.armendtahiraga.App;

import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

public interface Application {
    Response handleRequest(Request request);
}
