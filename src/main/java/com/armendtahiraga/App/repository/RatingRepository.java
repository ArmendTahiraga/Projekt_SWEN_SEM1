package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.models.Rating;

import java.sql.Connection;
import java.sql.SQLException;

public class RatingRepository {
    Connection connection;

    public RatingRepository() {
        this.connection = Database.getConnection();
    }

    public boolean rateMedia(Rating rating) throws SQLException {
        String statement = "insert into rating (user_id, media_id, stars, comment) values (?, ?, ?, ?)";
        try{
            var preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, rating.getUserID());
            preparedStatement.setInt(2, rating.getMediaID());
            preparedStatement.setInt(3, rating.getStars());
            preparedStatement.setString(4, rating.getComment());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            throw new SQLException("Error rating media", exception);
        }
    }
}
