package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.models.Rating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RatingRepository {
    Connection connection;

    public RatingRepository() {
        this.connection = Database.getConnection();
    }

    public boolean rateMedia(Rating rating) throws SQLException {
        String statement = "insert into ratings (user_id, media_id, stars, comment) values (?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
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

    public boolean likeRating(int ratingID) throws SQLException {
        System.out.println(ratingID);
        String statement = "update rating set likes = likes + 1 where rating_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, ratingID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            System.out.println(exception.getMessage());
            throw new SQLException("Error liking rating", exception);
        }
    }
}
