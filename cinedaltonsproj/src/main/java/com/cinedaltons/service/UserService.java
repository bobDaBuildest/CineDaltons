package com.cinedaltons.service;

import com.cinedaltons.dto.UserRegisterDTO;

public interface UserService {
    void registerUser(UserRegisterDTO userDto) throws Exception;
}