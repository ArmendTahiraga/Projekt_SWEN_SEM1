package com.armendtahiraga.App;

import com.armendtahiraga.App.models.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenRegistry {
    private static final Map<String, User> tokens = new ConcurrentHashMap<>();

    private TokenRegistry() { }

    public static User get(String token) {
        return tokens.get(token);
    }

    public static void bindUserToken(User user, String token) {
        tokens.entrySet().removeIf(entry -> entry.getValue().getUserID() == user.getUserID());
        tokens.put(token, user);
    }
}
