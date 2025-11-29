package com.cinedaltons.service.impl;

import com.movieapp.dto.UserRegisterDTO;
import com.movieapp.entity.User;
import com.movieapp.repository.UserRepository;
import com.movieapp.service.UserService;
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
}