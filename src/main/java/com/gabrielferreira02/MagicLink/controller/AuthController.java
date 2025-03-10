package com.gabrielferreira02.MagicLink.controller;

import com.gabrielferreira02.MagicLink.dto.*;
import com.gabrielferreira02.MagicLink.service.impl.AuthServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthServiceImplementation authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("register")
    public void register(@RequestBody RegisterRequestDTO request) {
        authService.createUser(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("login")
    public void login(@RequestBody LoginRequestDTO request) {
        authService.login(request);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("validate")
    public ValidateResponseDTO validateToken(@RequestBody ValidateRequestDTO request) {
        return authService.validateToken(request.token());
    }

}
