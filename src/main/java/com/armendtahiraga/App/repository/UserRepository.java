package com.armendtahiraga.App.repository;

import com.armendtahiraga.App.database.Database;
import com.armendtahiraga.App.models.User;

import java.sql.*;
import java.util.Optional;

public class UserRepository {
    Connection connection;

    public UserRepository(){
        this.connection = Database.getConnection();
    }

    public Optional<User> findById(int userId) throws SQLException {
        String statement = "select user_id, username, password_hash, email, \"token\" from \"user\" where user_id = ?";
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
        String statement = "select user_id, username, password_hash, email, \"token\" from \"user\" where username = ?";
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
        String statement = "insert into \"user\" (username, email, password_hash) values (?, ?, ?) returning user_id, username, password_hash, email, \"token\"";
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

    public User saveUserToken(int userId, String token) throws SQLException {
        System.out.println(token);
        String statement = "update \"user\" set \"token\" = ? where user_id = ? returning user_id, username, password_hash, email, \"token\"";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, token);
            preparedStatement.setInt(2, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new SQLException("User token update failed");
            }

            return mapUser(resultSet);
        } catch (SQLException exception) {
            throw new SQLException("Error saving user token", exception);
        }
    }

    public User findUserByToken(String token) throws SQLException {
        String statement = "select user_id, username, password_hash, email, \"token\" from \"user\" where \"token\" = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(statement);
            preparedStatement.setString(1, token);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next() ? mapUser(resultSet) : null;
        } catch (SQLException exception) {
            throw new SQLException("Error finding user by token", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getInt("user_id"),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getString("email")
        );

        if(resultSet.getString("token") != null){
            user.setToken(resultSet.getString("token"));
        }

        return user;
    }
}
