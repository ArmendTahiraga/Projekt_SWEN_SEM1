package com.armendtahiraga.App.Models;

import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int movieID;
    private int creatorUserId;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private int ageRestriction;
    private List<String> genres = new ArrayList<>();
    private double averageScore;
    private List<Rating> ratings = new ArrayList<>();

    public Movie(int movieID, int creatorUserId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) {
        this.movieID = movieID;
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genres = genres;
        this.averageScore = 0.0;
    }

    public int getMovieID() {
        return movieID;
    }

    public int getCreatorUserId() {
        return creatorUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getMediaType() {
        return mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public int getAgeRestriction() {
        return ageRestriction;
    }

    public List<String> getGenres() {
        return genres;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public List<Rating> getRatings() {
        return ratings;
    }
}
