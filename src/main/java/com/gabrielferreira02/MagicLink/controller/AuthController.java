package com.gabrielferreira02.MagicLink.controller;

import com.gabrielferreira02.MagicLink.dto.*;
import com.gabrielferreira02.MagicLink.service.impl.AuthServiceImplementation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthServiceImplementation authService;

    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO request) {
        try {
            authService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            authService.login(request);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("validate")
    public ResponseEntity<Object> validateToken(@RequestBody ValidateRequestDTO request) {
        try {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.validateToken(request.token()));
        } catch(RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
