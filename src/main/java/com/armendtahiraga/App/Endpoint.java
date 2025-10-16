package com.armendtahiraga.App;

import com.armendtahiraga.Server.Request;
import com.armendtahiraga.Server.Response;

@FunctionalInterface
public interface Endpoint {
    Response handle(Request request);
}
