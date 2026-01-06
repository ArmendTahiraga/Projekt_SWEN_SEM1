package at.technikum.application.repository;

import at.technikum.application.database.Database;
import at.technikum.application.models.Rating;
import at.technikum.application.util.RatingUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RatingRepository {
    Connection connection;

    public RatingRepository() {
        this.connection = Database.getConnection();
    }

    public boolean rateMedia(Rating rating) throws SQLException {
        String statement = "insert into rating (user_id, media_id, stars, comment, timestamp) values (?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, rating.getUserID());
            preparedStatement.setInt(2, rating.getMediaID());
            preparedStatement.setInt(3, rating.getStars());
            preparedStatement.setString(4, rating.getComment());
            preparedStatement.setString(5, rating.getTimestamp());

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

    public boolean confirmRatingComment(int ratingID, int creatorID) throws SQLException {
        String statement = "update rating set confirmed = true where rating_id = ? and user_id = ?";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, ratingID);
            preparedStatement.setInt(2, creatorID);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception exception){
            throw new SQLException("Error confirming rating comment", exception);
        }
    }

    public Optional<List<Rating>> getUserRatings(int userID) throws SQLException {
        try{
            String statement = "select * from rating where user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Rating> ratings = new ArrayList<>();
            while (resultSet.next()) {
                ratings.add(RatingUtil.mapRating(resultSet));
            }

            return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings);
        } catch (Exception exception){
            throw new SQLException("Error fetching user ratings", exception);
        }
    }
}
