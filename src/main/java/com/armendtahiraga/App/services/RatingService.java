package com.armendtahiraga.App.services;

import com.armendtahiraga.App.repository.RatingRepository;

public class RatingService {
    RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }
}
