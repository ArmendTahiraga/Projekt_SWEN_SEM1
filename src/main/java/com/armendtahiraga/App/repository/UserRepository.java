package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.models.User;

import java.sql.*;
import java.util.Arrays;
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
            return resultSet.next() ? Optional.of(mapUser(resultSet)) : Optional.empty();
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
            return resultSet.next() ? Optional.of(mapUser(resultSet)) : Optional.empty();
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

            return mapUser(resultSet);
        } catch (SQLException exception) {
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

            return resultSet.next() ? Optional.of(mapUser(resultSet)) : Optional.empty();
        } catch (SQLException exception) {
            throw new SQLException("Error updating user", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("email"),
                resultSet.getString("favorite_genre")
        );

        return user;
    }
}
