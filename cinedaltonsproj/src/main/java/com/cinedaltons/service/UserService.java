package com.movieapp.service;

import com.movieapp.dto.UserRegisterDTO;

public interface UserService {
    void registerUser(UserRegisterDTO userDto) throws Exception;
}