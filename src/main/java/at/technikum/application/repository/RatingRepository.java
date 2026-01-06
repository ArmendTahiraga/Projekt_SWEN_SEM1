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
        try {
            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, rating.getUserID());
                preparedStatement.setInt(2, rating.getMediaID());
                preparedStatement.setInt(3, rating.getStars());
                preparedStatement.setString(4, rating.getComment());
                preparedStatement.setString(5, rating.getTimestamp());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            updateMediaAverageRating(rating.getMediaID());

            connection.commit();
            return true;

        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {}

            throw new SQLException("Error rating media", exception);
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    public boolean likeRating(int ratingID) throws SQLException {
        String statement = "update rating set likes = likes + 1 where rating_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, ratingID);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new SQLException("Error liking rating", exception);
        }
    }

    public boolean updateRating(int ratingID, int creatorID, int stars, String comment) throws SQLException {
        String statement = "update rating set stars = ?, comment = ? where rating_id = ? and user_id = ?";
        try {
            connection.setAutoCommit(false);

            int mediaId = getMediaIdByRatingId(ratingID);

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, stars);
                preparedStatement.setString(2, comment);
                preparedStatement.setInt(3, ratingID);
                preparedStatement.setInt(4, creatorID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            updateMediaAverageRating(mediaId);

            connection.commit();
            return true;

        } catch (Exception exception) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {}

            throw new SQLException("Error updating rating", exception);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    public boolean deleteRating(int ratingID, int creatorID) throws SQLException {
        String statement = "delete from rating where rating_id = ? and user_id = ?";
        try {
            connection.setAutoCommit(false);

            int mediaId = getMediaIdByRatingId(ratingID);

            try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
                preparedStatement.setInt(1, ratingID);
                preparedStatement.setInt(2, creatorID);

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected <= 0) {
                    connection.rollback();
                    return false;
                }
            }

            updateMediaAverageRating(mediaId);

            connection.commit();
            return true;

        } catch (Exception exception) {
            try {
                connection.rollback(); }
            catch (SQLException ignored) {}

            throw new SQLException("Error deleting rating", exception);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    public boolean confirmRatingComment(int ratingID, int creatorID) throws SQLException {
        String statement = "update rating set confirmed = true where rating_id = ? and user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, ratingID);
            preparedStatement.setInt(2, creatorID);
            return preparedStatement.executeUpdate() > 0;
        } catch (Exception exception) {
            throw new SQLException("Error confirming rating comment", exception);
        }
    }

    public Optional<List<Rating>> getUserRatings(int userID) throws SQLException {
        try {
            String statement = "select * from rating where user_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Rating> ratings = new ArrayList<>();
            while (resultSet.next()) {
                ratings.add(RatingUtil.mapRating(resultSet));
            }

            return ratings.isEmpty() ? Optional.empty() : Optional.of(ratings);
        } catch (Exception exception) {
            throw new SQLException("Error fetching user ratings", exception);
        }
    }

    private void updateMediaAverageRating(int mediaId) throws SQLException {
        String statement = "update media set average_rating = (select COALESCE(AVG(stars)::real, 0) from rating where media_id = ?) where media_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, mediaId);
            preparedStatement.setInt(2, mediaId);
            preparedStatement.executeUpdate();
        }
    }

    private int getMediaIdByRatingId(int ratingId) throws SQLException {
        String statement = "select media_id from rating where rating_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(statement)) {
            preparedStatement.setInt(1, ratingId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException("Rating not found: " + ratingId);
            }

            return resultSet.getInt("media_id");
        }
    }
}