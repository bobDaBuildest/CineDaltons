package com.cinedaltons.controller;

import com.cinedaltons.dto.SignupRequest;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public Map<String, String> signup(@RequestBody SignupRequest request) {

        Map<String, String> response = new HashMap<>();

        if (userRepository.existsByUsername(request.username)) {
            response.put("status", "error");
            response.put("message", "Username already exists");
            return response;
        }

        User user = new User();
        user.setUsername(request.username);
        user.setFirstName(request.firstName);
        user.setLastName(request.lastName);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password)); // hash password

        userRepository.save(user);

        response.put("status", "success");
        return response;
    }
}
