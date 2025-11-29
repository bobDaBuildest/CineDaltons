package com.cinedaltons.service.impl;

import com.movieapp.dto.UserRegisterDTO;
import com.movieapp.entity.User;
import com.movieapp.repository.UserRepository;
import com.movieapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface MovieSearchService {
    List<Movie> searchMovies(String query);
    // to do
}