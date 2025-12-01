package com.armendtahiraga.App.models;

import java.util.ArrayList;
import java.util.List;

public class Media {
    private int mediaID;
    private int creatorUserId;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private int ageRestriction;
    private List<String> genres = new ArrayList<>();
    private double averageScore;
    private List<Rating> ratings = new ArrayList<>();

    public Media(int mediaID, int creatorUserId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) {
        this.mediaID = mediaID;
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genres = genres;
        this.averageScore = 0.0;
    }

    public Media(int creatorUserId, String title, String description, String mediaType, int releaseYear, int ageRestriction, List<String> genres) {
        this.creatorUserId = creatorUserId;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.ageRestriction = ageRestriction;
        this.genres = genres;
        this.averageScore = 0.0;
    }

    public int getMediaID() {
        return mediaID;
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
