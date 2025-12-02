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
        String statement = "insert into rating (user_id, media_id, stars, comment) values (?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, rating.getUserID());
            preparedStatement.setInt(2, rating.getMediaID());
            preparedStatement.setInt(3, rating.getStars());
            preparedStatement.setString(4, rating.getComment());

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            System.out.println(exception.getMessage());
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

    public boolean updateRating(int ratingID, int creatorID, int stars, String comment) throws SQLException {
        String statement = "update rating set stars = ?, comment = ? where rating_id = ? and user_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, stars);
            preparedStatement.setString(2, comment);
            preparedStatement.setInt(3, ratingID);
            preparedStatement.setInt(4, creatorID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            throw new SQLException("Error updating rating", exception);
        }
    }

    public boolean deleteRating(int ratingID, int creatorID) throws SQLException {
        String statement = "delete from rating where rating_id = ? and user_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, ratingID);
            preparedStatement.setInt(2, creatorID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            throw new SQLException("Error deleting rating", exception);
        }
    }
}
