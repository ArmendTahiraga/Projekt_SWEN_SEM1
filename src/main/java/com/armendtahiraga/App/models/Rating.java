package com.armendtahiraga.App.models;

public class Rating {
    private int ratingID;
    private int mediaID;
    private int userID;
    private int stars;
    private String comment;
    private String timestamp;
    public boolean confirmed;
    public int likes;

    public Rating(int ratingID, int mediaID, int userID, int stars, String comment, String timestamp, int likes, boolean confirmed) {
        this.ratingID = ratingID;
        this.mediaID = mediaID;
        this.userID = userID;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = timestamp;
        this.likes = likes;
        this.confirmed = confirmed;
    }

    public Rating(int mediaID, int userID, int stars, String comment, String timestamp) {
        this.mediaID = mediaID;
        this.userID = userID;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getRatingID() {
        return ratingID;
    }

    public int getMediaID() {
        return mediaID;
    }

    public int getUserID() {
        return userID;
    }

    public int getStars() {
        return stars;
    }

    public String getComment() {
        return comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public int getLikes() {
        return likes;
    }
}
