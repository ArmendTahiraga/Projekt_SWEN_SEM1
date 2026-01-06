package at.technikum.application.services;

import at.technikum.application.models.Media;
import at.technikum.application.repository.FavoritesRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class FavoritesService {
    FavoritesRepository favoritesRepository;

    public FavoritesService(FavoritesRepository favoritesRepository) {
        this.favoritesRepository = favoritesRepository;
    }

    public List<Media> getUserFavorites(int userID) throws SQLException {
        try{
            Optional<List<Media>> favoriteMedias = favoritesRepository.getUserFavorites(userID);

            if (favoriteMedias.isEmpty()) {
                throw new IllegalArgumentException("No favorites found");
            }

            return favoriteMedias.get();
        } catch (SQLException exception){
            throw new SQLException("DB error during fetching user favorites", exception);
        }
    }

    public boolean addMediaToFavorites(int userID, int mediaID) {
        try{
            return favoritesRepository.addMediaToFavorites(userID, mediaID);
        } catch (Exception exception){
            return false;
        }
    }

    public boolean removeMediaFromFavorites(int userID, int mediaID) {
        try{
            return favoritesRepository.removeMediaFromFavorites(userID, mediaID);
        } catch (Exception exception){
            return false;
        }
    }
}
