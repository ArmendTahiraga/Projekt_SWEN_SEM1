package com.armendtahiraga.App.services;

import com.armendtahiraga.App.repository.FavoritesRepository;

public class FavoritesService {
    FavoritesRepository favoritesRepository;

    public FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }

    public boolean addMediaToFavorites(int userID, int mediaID) {
        try{
            return favoritesRepository.addMediaToFavorites(userID, mediaID);
        } catch (Exception exception){
            return false;
        }
    }
}
