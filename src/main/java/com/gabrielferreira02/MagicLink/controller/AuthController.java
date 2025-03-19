package com.gabrielferreira02.MagicLink.controller;

import com.gabrielferreira02.MagicLink.dto.*;
import com.gabrielferreira02.MagicLink.service.impl.AuthServiceImplementation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Controller to manage register and login with a magic link")
@RequestMapping("api/auth")
public class AuthController {

    private final AuthServiceImplementation authService;

    @Operation(
            summary = "Register a user",
            description = "Create a new user in the application",
            tags = "Authentication",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body request with username and email address",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequestDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "User created with success",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Error creating a new account",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping("register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequestDTO request) {
        authService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Login a user",
            description = "Validate a user and sent a access link to user email",
            tags = "Authentication",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body request with email address",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequestDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "Magic link sent with success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Fail to send link access",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping("login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO request) {
        authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(
            summary = "Validate a token",
            description = "Validate a token to check if token is valid or expired",
            tags = "Authentication",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Body request with token",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = ValidateRequestDTO.class),
                            mediaType = "application/json"
                    )
            ),
            responses = {
                    @ApiResponse(
                            description = "User created with success",
                            responseCode = "201",
                            content = @Content(
                                    schema = @Schema(implementation = ValidateResponseDTO.class),
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(
                            description = "Token is invalid",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping("validate")
    public ResponseEntity<Object> validateToken(@RequestBody ValidateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.validateToken(request.token()));
    }

}
