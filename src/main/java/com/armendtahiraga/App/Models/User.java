package com.armendtahiraga.App.Models;

import java.util.List;

public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String email;
    private String favoriteGenre;
    private List<Movie> favoriteMovies;
    private List<String> recommendations;

    public User(int userID, String username, String passwordHash, String email, String favoriteGenre, List<Movie> favoriteMovies, List<String> recommendations) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.favoriteGenre = favoriteGenre;
        this.favoriteMovies = favoriteMovies;
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

    public List<Movie> getFavoriteMovies() {
        return favoriteMovies;
    }

    public List<String> getRecommendations() {
        return recommendations;
    }
}
