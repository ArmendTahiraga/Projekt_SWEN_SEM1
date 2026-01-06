package at.technikum.application.services;

import at.technikum.application.models.Rating;
import at.technikum.application.repository.RatingRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RatingService {
    RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public boolean rateMedia(int userID, int mediaID, int stars, String comment) {
        try{
            String timestamp = LocalDateTime.now().toString();
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
            return false;
        }
    }

    public boolean updateRating(int ratingID, int creatorID, int stars, String comment) {
        try{
            return ratingRepository.updateRating(ratingID, creatorID, stars, comment);
        } catch (Exception exception){
            return false;
        }
    }

    public boolean deleteRating(int ratingID, int creatorID) {
        try{
            return ratingRepository.deleteRating(ratingID, creatorID);
        } catch (Exception exception){
            return false;
        }
    }

    public boolean confirmRatingComment(int ratingID, int creatorID) {
        try{
            return ratingRepository.confirmRatingComment(ratingID, creatorID);
        } catch (Exception exception){
            return false;
        }
    }

    public List<Rating> getUserRatings(int userID) {
        try{
            Optional<List<Rating>> ratings = ratingRepository.getUserRatings(userID);
            if (ratings.isEmpty()) {
                throw new IllegalArgumentException("User has no ratings");
            }

            return ratings.get();
        } catch (Exception exception){
            return null;
        }
    }
}
