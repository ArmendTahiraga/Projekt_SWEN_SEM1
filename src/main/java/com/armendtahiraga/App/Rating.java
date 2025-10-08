package com.armendtahiraga.App;

public class Rating {
    private int ratingID;
    private int movieID;
    private int userID;
    private int stars;
    private String comment;
    private String timestamp;

    public Rating(int ratingID, int movieID, int userID, int stars, String comment, String timestamp) {
        this.ratingID = ratingID;
        this.movieID = movieID;
        this.userID = userID;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public int getRatingID() {
        return ratingID;
    }

    public int getMovieID() {
        return movieID;
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
}
