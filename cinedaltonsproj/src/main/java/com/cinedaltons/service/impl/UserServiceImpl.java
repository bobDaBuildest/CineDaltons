package com.cinedaltons.service.impl;

import com.cinedaltons.dto.UserRegisterDTO;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import com.cinedaltons.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void registerUser(UserRegisterDTO userDto) throws Exception {

        // 1. Έλεγχος αν το username υπάρχει ήδη
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new Exception("Το username '" + userDto.getUsername() + "' υπάρχει ήδη.");
        }

        // 2. Μεταφορά δεδομένων από DTO σε Entity
        User newUser = new User();
        newUser.setUsername(userDto.getUsername());
        newUser.setPassword(userDto.getPassword()); // Εδώ κανονικά θέλει hashing
        newUser.setEmail(userDto.getEmail());

        // 3. Αποθήκευση
        userRepository.save(newUser);
    }
}