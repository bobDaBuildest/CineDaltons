package com.cinedaltons.service.impl;

import com.movieapp.dto.UserRegisterDTO;
import com.movieapp.entity.User;
import com.movieapp.repository.UserRepository;
import com.movieapp.service.UserService;
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

 public setMovieResponseDTO(String username, String email, String registrationDate, int totalQuizScore) {
        this.username = username;
        this.email = email;
        this.registrationDate = registrationDate;
        this.totalQuizScore = totalQuizScore;

 }


}