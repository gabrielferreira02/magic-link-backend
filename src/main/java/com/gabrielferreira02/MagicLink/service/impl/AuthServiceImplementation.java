package com.gabrielferreira02.MagicLink.service.impl;

import com.gabrielferreira02.MagicLink.dto.LoginRequestDTO;
import com.gabrielferreira02.MagicLink.dto.RegisterRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateResponseDTO;
import com.gabrielferreira02.MagicLink.entity.UserEntity;
import com.gabrielferreira02.MagicLink.exception.EmailAlreadyExistsException;
import com.gabrielferreira02.MagicLink.exception.InvalidTokenException;
import com.gabrielferreira02.MagicLink.exception.TokenNotFoundException;
import com.gabrielferreira02.MagicLink.exception.UserNotFoundException;
import com.gabrielferreira02.MagicLink.repository.UserRepository;
import com.gabrielferreira02.MagicLink.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    @Override
    public void createUser(RegisterRequestDTO request) {
        log.info("Attempting to create user with email: {}", request.email());

        if(userRepository.existsByEmail(request.email())) {
            log.warn("Email address already exists: {}", request.email());
            throw new EmailAlreadyExistsException("Email address already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());

        userRepository.save(newUser);
        log.info("User created successfully with email: {}", request.email());
    }

    @Override
    public ValidateResponseDTO validateToken(String token) {
        log.info("Validating token: {}", token);

        Optional<UserEntity> user = userRepository.findByValidationToken(token);

        if(user.isEmpty()) {
            log.warn("Token not found: {}", token);
            throw new TokenNotFoundException("Token not found");
        }

        Date tokenExpiresDate = user.get().getExpirationTime();
        Date now = new Date();

        if(tokenExpiresDate.before(now)) {
            log.warn("Token expired: {}", token);
            throw new InvalidTokenException("Token is not valid. Please, try login again");
        }

        log.info("Token validated successfully for user: {}", user.get().getUsername());
        return new ValidateResponseDTO(user.get().getUsername(), true);
    }

    public void login(LoginRequestDTO request) {
        log.info("Attempting login for user with email: {}", request.email());

        Optional<UserEntity> user = userRepository.findByEmail(request.email());

        if(user.isEmpty()) {
            log.warn("User not found with email: {}", request.email());
            throw new UserNotFoundException("User not found");
        }

        user.get().setValidationToken(UUID.randomUUID().toString());
        user.get().setExpirationTime(new Date(System.currentTimeMillis() + (10*60*1000)));
        userRepository.save(user.get());

        log.info("Login successful for user: {}", user.get().getUsername());
        emailService.sendEmail(request.email(), user.get().getUsername(), user.get().getValidationToken());
    }
}
