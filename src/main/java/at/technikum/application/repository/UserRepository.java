package at.technikum.application.repository;

import at.technikum.application.database.Database;
import at.technikum.application.models.Media;
import at.technikum.application.models.User;
import at.technikum.application.util.MediaUtil;
import at.technikum.application.util.UserUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository {
    Connection connection;

    public UserRepository(){
        this.connection = Database.getConnection();
    }

    public Optional<User> findById(int userId) throws SQLException {
        String statement = "select user_id, username, password_hash, email, favorite_genre from \"user\" where user_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? Optional.of(UserUtil.mapUser(resultSet)) : Optional.empty();
        } catch (SQLException exception) {
            throw new SQLException("Error finding user by ID", exception);
        }
    }

    public Optional<User> findByUsername(String username) throws SQLException {
        String statement = "select user_id, username, password_hash, email, favorite_genre from \"user\" where username = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? Optional.of(UserUtil.mapUser(resultSet)) : Optional.empty();
        } catch (SQLException exception) {
            throw new SQLException(exception);
        }
    }

    public User create(String username, String email, String passwordHash) throws SQLException {
        String statement = "insert into \"user\" (username, email, password_hash) values (?, ?, ?) returning user_id, username, password_hash, email, favorite_genre";
        try {
            PreparedStatement preparedStatement = UserRepository.this.connection.prepareStatement(statement);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, passwordHash);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException("User insert failed");
            }

            return UserUtil.mapUser(resultSet);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            throw new SQLException("Error creating user", exception);
        }
    }

    public Optional<User> updateUser(int id, String email, String favoriteGenre) throws SQLException {
        String statement = "update \"user\" set email = ?, favorite_genre = ? where user_id = ? returning user_id, username, password_hash, email, favorite_genre";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, favoriteGenre);
            preparedStatement.setInt(3, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next() ? Optional.of(UserUtil.mapUser(resultSet)) : Optional.empty();
        } catch (SQLException exception) {
            throw new SQLException("Error updating user", exception);
        }
    }

    public Optional<List<User>> getLeaderboard() throws SQLException {
        String statement = "select user_id, username, password_hash, email, favorite_genre from \"user\" order by (select count(*) from rating where rating.user_id = \"user\".user_id) desc limit 10";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<User> leaderboard = new ArrayList<>();
            while (resultSet.next()) {
                leaderboard.add(UserUtil.mapUser(resultSet));
            }

            return leaderboard.isEmpty() ? Optional.empty() : Optional.of(leaderboard);
        } catch (SQLException exception) {
            throw new SQLException("Error getting leaderboard", exception);
        }
    }

    public Optional<List<Media>> getRecommendations(int userID, String type) throws SQLException {
        if ("genre".equals(type)) {
            return getGenreBasedRecommendations(userID);
        } else {
            return getContentBasedRecommendations(userID);
        }
    }

    private Optional<List<Media>> getGenreBasedRecommendations(int userID) throws SQLException {
        String statement = "select mediaRecommended.* from media mediaRecommended where mediaRecommended.genres = " +
                                "(select genres from media mediaRated where mediaRated.media_id = " +
                                    "(select rating.media_id from rating where rating.user_id = ? and rating.stars >= 4 order by rating.rating_id desc limit 1)) " +
                            "and mediarecommended.media_id != " +
                                "(select rating.media_id from rating where rating.user_id = ? order by rating.rating_id desc limit 1)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Media> recommendations = new ArrayList<>();
            while (resultSet.next()) {
                recommendations.add(MediaUtil.mapMedia(resultSet));
            }

            return recommendations.isEmpty() ? Optional.empty() : Optional.of(recommendations);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            throw new SQLException("Error getting recommendation", exception);
        }
    }

    private Optional<List<Media>> getContentBasedRecommendations(int userID) throws SQLException {
        String statement = "select mediaRecommended.* from media mediaRecommended where " +
                "mediaRecommended.genres = " +
                        "(select genres from media mediaRated where mediaRated.media_id = " +
                                    "(select rating.media_id from rating where rating.user_id = ? and rating.stars >= 4 order by rating.rating_id desc limit 1)) " +
                "and mediaRecommended.media_type = " +
                        "(select media_type from media mediaRated where mediaRated.media_id = " +
                                    "(select rating.media_id from rating where rating.user_id = ? and rating.stars >= 4 order by rating.rating_id desc limit 1) " +
                "and mediaRecommended.age_restriction = " +
                        "(select age_restriction from media mediaRated where mediaRated.media_id = " +
                                    "(select rating.media_id from rating where rating.user_id = ? and rating.stars >= 4 order by rating.rating_id desc limit 1) " +
                "and mediarecommended.media_id != " +
                        "(select rating.media_id from rating where rating.user_id = ? order by rating.rating_id desc limit 1)";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, userID);

            ResultSet resultSet = preparedStatement.executeQuery();
            List<Media> recommendations = new ArrayList<>();
            while (resultSet.next()) {
                recommendations.add(MediaUtil.mapMedia(resultSet));
            }

            return recommendations.isEmpty() ? Optional.empty() : Optional.of(recommendations);
        } catch (SQLException exception) {
            System.out.println(exception.getMessage());
            throw new SQLException("Error getting recommendation", exception);
        }
    }
}
