package com.armendtahiraga.App.services;

import com.armendtahiraga.App.models.Rating;
import com.armendtahiraga.App.repository.RatingRepository;

import java.time.LocalDate;

public class RatingService {
    RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public boolean rateMedia(int userID, int mediaID, int stars, String comment) {
        try{
            String timestamp = LocalDate.now().toString();
            Rating rating = new Rating(mediaID, userID, stars, comment, timestamp);
            return ratingRepository.rateMedia(rating);
        } catch (Exception exception){
            return false;
        }
    }

    public boolean likeRating(int ratingID) {
        try{
            return ratingRepository.likeRating(ratingID);
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            return false;
        }
    }
}
