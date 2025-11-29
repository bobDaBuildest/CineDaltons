package com.cinedaltons.service;

import com.cinedaltons.dto.UserRegisterDTO;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import com.cinedaltons.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface MovieSearchService {
    List<Movie> searchMovies(String query);
    // to do
}