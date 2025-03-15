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
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    @Override
    public void createUser(RegisterRequestDTO request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email address already exists");
        }

        UserEntity newUser = new UserEntity();
        newUser.setUsername(request.username());
        newUser.setEmail(request.email());

        userRepository.save(newUser);
    }

    @Override
    public ValidateResponseDTO validateToken(String token) {
        Optional<UserEntity> user = userRepository.findByValidationToken(token);

        if(user.isEmpty()) {
            throw new TokenNotFoundException("Token not found");
        }

        Date tokenExpiresDate = user.get().getExpirationTime();
        Date now = new Date();

        if(tokenExpiresDate.before(now)) {
            throw new InvalidTokenException("Token is not valid. Please, try login again");
        }

        return new ValidateResponseDTO(user.get().getUsername(), true);
    }

    public void login(LoginRequestDTO request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.email());

        if(user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        user.get().setValidationToken(UUID.randomUUID().toString());
        user.get().setExpirationTime(new Date(System.currentTimeMillis() + (10*60*1000)));
        userRepository.save(user.get());
        emailService.sendEmail(request.email(), user.get().getUsername(), user.get().getValidationToken());
    }
}
