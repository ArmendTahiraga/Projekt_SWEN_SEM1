package com.armendtahiraga.App.services;

import com.armendtahiraga.App.MRPApplication;
import com.armendtahiraga.App.models.User;
import com.armendtahiraga.App.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Optional;

public class AuthService {
    UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(String username, String email, String password){
        try {
            System.out.println("Registering user: " + username + ", " + email);
            if (userRepository.findByUsername(username).isPresent()) {
                System.out.println("Username already taken: " + username);
                throw new IllegalArgumentException("Username already taken");
            }

            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            System.out.println(hash);
            User createdUser = userRepository.create(username, email, hash);
            System.out.println(createdUser.getUserID() + " + " + createdUser.getUsername() + " + " + createdUser.getEmail() + " + " + createdUser.getPasswordHash());
            String token = newToken(username);

            createdUser.setToken(token);
            createdUser = userRepository.saveUserToken(createdUser.getUserID(), token);
            MRPApplication.setLoggedInUser(createdUser);

            return createdUser;
        } catch (SQLException exception) {
            throw new RuntimeException("Error registering user" + exception);
        }
    }

    public User login(String username, String password) {
        try {
            Optional<User> userOptional = userRepository.findByUsername(username);
            if (userOptional.isEmpty()) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            User user = userOptional.get();
            if (user.getPasswordHash() == null || !BCrypt.checkpw(password, user.getPasswordHash())) {
                throw new IllegalArgumentException("Invalid username or password");
            }

            String token = newToken(username);
            user.setToken(token);
            user = userRepository.saveUserToken(user.getUserID(), token);
            MRPApplication.setLoggedInUser(user);

            return user;
        } catch (SQLException exception) {
            throw new RuntimeException("DB error during login", exception);
        }
    }

    public User verifyToken(String token) {
        try {
            User user = userRepository.findUserByToken(token);

            if(user == null){
                throw new IllegalArgumentException("Invalid or expired token");
            } else {
                return user;
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB error during token verification", e);
        }
    }

    private String newToken(String username) {
        return username + "-mrpToken";
    }
}
