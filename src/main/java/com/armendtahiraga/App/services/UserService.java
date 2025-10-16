package com.armendtahiraga.App.services;

import com.armendtahiraga.App.repository.UserRepository;

public class UserService {
    UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
