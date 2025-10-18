package com.armendtahiraga.App.models;

import java.util.List;

public class User {
    private int userID;
    private String username;
    private String passwordHash;
    private String email;
    private String favoriteGenre;
    private List<Media> favoriteMedias;
    private String token;

    public User(int userID, String username, String passwordHash, String email) {
        this.userID = userID;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
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

    public List<Media> getFavoriteMedias() {
        return favoriteMedias;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setFavoriteGenre(String favoriteGenre) {
        this.favoriteGenre = favoriteGenre;
    }

    public void setFavoriteMedias(List<Media> favoriteMedias) {
        this.favoriteMedias = favoriteMedias;
    }
}
