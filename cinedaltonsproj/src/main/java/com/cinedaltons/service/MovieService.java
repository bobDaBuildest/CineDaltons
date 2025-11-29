/**package com.cinedaltons.service;

import com.cinedaltons.dto.UserRegisterDTO;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import com.cinedaltons.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public class MovieService {
    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public boolean movieExists(String title) {
        return repository.findByTitle(title).isPresent();
    }
}**/