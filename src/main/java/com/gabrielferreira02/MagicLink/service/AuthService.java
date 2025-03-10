package com.gabrielferreira02.MagicLink.service;

import com.gabrielferreira02.MagicLink.dto.LoginRequestDTO;
import com.gabrielferreira02.MagicLink.dto.RegisterRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateResponseDTO;

public interface AuthService {
    void createUser(RegisterRequestDTO request);
    ValidateResponseDTO validateToken(String token);
    void login(LoginRequestDTO request);
}
