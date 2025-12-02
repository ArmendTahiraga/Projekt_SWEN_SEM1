package com.armendtahiraga.App.services;

import com.armendtahiraga.App.repository.FavoritesRepository;

public class FavoritesService {
    FavoritesRepository favoritesRepository;

    public FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }
}
