package com.gabrielferreira02.MagicLink.service.impl;

import com.gabrielferreira02.MagicLink.dto.LoginRequestDTO;
import com.gabrielferreira02.MagicLink.dto.RegisterRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateResponseDTO;
import com.gabrielferreira02.MagicLink.entity.UserEntity;
import com.gabrielferreira02.MagicLink.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplementationTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmailServiceImpl emailService;
    @InjectMocks
    private AuthServiceImplementation authService;

    @Nested
    class createUser {
        @Test
        @DisplayName("It should create a user successfully")
        void success() {
            RegisterRequestDTO request = new RegisterRequestDTO("User", "user@email.com");

            when(userRepository.existsByEmail(request.email())).thenReturn(false);

            authService.createUser(request);

            verify(userRepository, times(1)).save(any(UserEntity.class));
        }

        @Test
        @DisplayName("It should fail on create a new user because email already exists")
        void errorCase1() {
            RegisterRequestDTO request = new RegisterRequestDTO("User", "user@email.com");

            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            assertThrows(RuntimeException.class, () -> {
                authService.createUser(request);
            });
        }

    }

    @Nested
    class validateToken {

        @Test
        @DisplayName("It should validate a token with success")
        void success() {
            String token = UUID.randomUUID().toString();

            UserEntity user = new UserEntity();
            user.setUsername("User");
            user.setEmail("user@email.com");
            user.setValidationToken(token);
            user.setExpirationTime(new Date(System.currentTimeMillis() + 10000));

            when(userRepository.findByValidationToken(token)).thenReturn(Optional.of(user));

            ValidateResponseDTO response = authService.validateToken(token);

            assertNotNull(response);
            assertTrue(response.isValid());
            assertEquals(user.getUsername(), response.username());
        }

        @Test
        @DisplayName("It should fail on validate a token because token is expired")
        void errorCase1() {
            String token = UUID.randomUUID().toString();

            UserEntity user = new UserEntity();
            user.setUsername("User");
            user.setEmail("user@email.com");
            user.setValidationToken(token);
            user.setExpirationTime(new Date(System.currentTimeMillis() - 10000));

            when(userRepository.findByValidationToken(token)).thenReturn(Optional.of(user));

            assertThrows(RuntimeException.class, () -> {
                authService.validateToken(token);
            });
        }

        @Test
        @DisplayName("It should fail on validate a token because token not found")
        void errorCase2() {
            String token = UUID.randomUUID().toString();

            when(userRepository.findByValidationToken(token)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                authService.validateToken(token);
            });

        }
    }

    @Nested
    class login {

        @Test
        @DisplayName("It should login a user successfully")
        void success() {
            UserEntity user = new UserEntity();
            user.setUsername("User");
            user.setEmail("user@email.com");
            user.setValidationToken(null);
            user.setExpirationTime(null);

            LoginRequestDTO request = new LoginRequestDTO(user.getEmail());

            when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

            authService.login(request);

            assertNotNull(user.getExpirationTime());
            assertNotNull(user.getValidationToken());

            verify(emailService, times(1)).sendEmail(user.getEmail(), user.getUsername(), user.getValidationToken());
            verify(userRepository, times(1)).save(user);
        }

        @Test
        @DisplayName("It should fail in login a user because email does not exists")
        void errorCase1() {
            LoginRequestDTO request = new LoginRequestDTO("error@email.com");

            when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                authService.login(request);
            });
        }
    }
}