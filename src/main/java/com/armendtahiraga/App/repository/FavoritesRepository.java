package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;

import java.sql.Connection;

public class FavoritesRepository {
    Connection connection;

    public FavoritesRepository(){
        this.connection = Database.getConnection();
    }

    public boolean addMediaToFavorites(int userID, int mediaID) {
        String statement = "insert into favorite (user_id, media_id) values (?, ?)";
        try{
            var preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, mediaID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            return false;
        }
    }
}
