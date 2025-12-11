package com.cinedaltons.dto;

import com.cinedaltons.dto.UserRegisterDTO;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import com.cinedaltons.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class MovieResponseDTO {
    private String username, email, registrationDate;
    private int totalQuizScore;


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRegistrationDate() {
        return registrationDate;

    }

    public int getTotalQuizScore() {
        return totalQuizScore;
    }

    public MovieResponseDTO(String username, String email, String registrationDate, int totalQuizScore) {
        this.username = username;
        this.email = email;
        this.registrationDate = registrationDate;
        this.totalQuizScore = totalQuizScore;

    }


}