package com.armendtahiraga.App.models;

import java.util.List;

public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String email;
    private String favoriteGenre;
    private List<Media> favoriteMedia;
    private List<String> recommendations;

    public User(int userID, String username, String passwordHash, String email, String favoriteGenre, List<Media> favoriteMedia, List<String> recommendations) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
        this.favoriteMedia = favoriteMedia;
        this.recommendations = recommendations;
    }

    public int getUserID() {
        return userID;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public String getFavoriteGenre() {
        return favoriteGenre;
    }

    public List<Media> getFavoriteMovies() {
        return favoriteMedia;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
