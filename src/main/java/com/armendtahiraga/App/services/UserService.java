package com.armendtahiraga.App.services;

import com.armendtahiraga.App.exceptions.NotFoundException;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByID(int userID) {
        try{
            Optional<User> user = userRepository.findById(userID);
            if (user.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }

            return user.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during fetching user by ID");
        }
    }

    public User updateUser(int userID, String email, String favoriteGenre) {
        try{
            Optional<User> updatedUser = userRepository.updateUser(userID, email, favoriteGenre);
            if (updatedUser.isEmpty()) {
                throw new IllegalArgumentException("User not found for update");
            }

            return updatedUser.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during updating user profile");
        }
    }

    public List<User> getLeaderboard(){
        try{
            Optional<List<User>> leaderboard = userRepository.getLeaderboard();

            if (leaderboard.isEmpty()) {
                throw new NotFoundException("No leaderboard data found");
            }

            return leaderboard.get();
        } catch (SQLException exception){
            throw new RuntimeException("DB error during fetching leaderboard");
        }
    }
}
