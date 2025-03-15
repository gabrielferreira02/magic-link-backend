package com.gabrielferreira02.MagicLink.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gabrielferreira02.MagicLink.dto.LoginRequestDTO;
import com.gabrielferreira02.MagicLink.dto.RegisterRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateResponseDTO;
import com.gabrielferreira02.MagicLink.entity.UserEntity;
import com.gabrielferreira02.MagicLink.service.impl.AuthServiceImplementation;
import com.gabrielferreira02.MagicLink.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthServiceImplementation authService;
    @MockitoBean
    private EmailServiceImpl emailService;

    @Test
    @DisplayName("It should create a user successfully")
    void registerSuccess() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("User", "user@email.com");
        String body = objectMapper.writeValueAsString(request);

        doNothing().when(authService).createUser(request);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

    }

    @Test
    @DisplayName("It should fail because email already exists")
    void registerErrorCase1() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("User", "user@email.com");
        String body = objectMapper.writeValueAsString(request);

        doThrow(RuntimeException.class).when(authService).createUser(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should fail because user is empty")
    void registerErrorCase2() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("", "user@email.com");
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should fail because email is empty")
    void registerErrorCase3() throws Exception {
        RegisterRequestDTO request = new RegisterRequestDTO("User", "");
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should login a user successfully")
    void loginSuccess() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("user@email.com");
        String body = objectMapper.writeValueAsString(request);

        doNothing().when(authService).login(request);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("It should fail because email is empty")
    void loginErrorCase1() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("");
        String body = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should fail because user doesn't exists")
    void loginErrorCase2() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("");
        String body = objectMapper.writeValueAsString(request);

        doThrow(RuntimeException.class).when(authService).login(request);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should validate a token successfully")
    void validateTokenSuccess() throws Exception {
        ValidateRequestDTO request = new ValidateRequestDTO(UUID.randomUUID().toString());
        String body = objectMapper.writeValueAsString(request);

        UserEntity user = new UserEntity();
        user.setUsername("User");
        user.setEmail("user@email.com");
        user.setValidationToken(request.token());
        user.setExpirationTime(new Date(System.currentTimeMillis() - 5000));

        when(authService.validateToken(request.token())).thenReturn(
                new ValidateResponseDTO(user.getUsername(), true)
        );

        mockMvc.perform(post("/api/auth/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isAccepted())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value(user.getUsername()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isValid").value(true));
    }

    @Test
    @DisplayName("It should invalidate a token because doesn't exists")
    void validateTokenErrorCase1() throws Exception {
        ValidateRequestDTO request = new ValidateRequestDTO("invalid token");
        String body = objectMapper.writeValueAsString(request);

        doThrow(new RuntimeException("Token not found")).when(authService).validateToken(request.token());

        mockMvc.perform(post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token not found"));
    }

    @Test
    @DisplayName("It should invalidate a token because token is expired")
    void validateTokenErrorCase2() throws Exception {
        ValidateRequestDTO request = new ValidateRequestDTO("invalid token");
        String body = objectMapper.writeValueAsString(request);

        UserEntity user = new UserEntity();
        user.setUsername("User");
        user.setEmail("user@email.com");
        user.setValidationToken(request.token());
        user.setExpirationTime(new Date(System.currentTimeMillis() + 5000));

        doThrow(new RuntimeException("Token is not valid. Please, try login again")).when(authService).validateToken(request.token());

        mockMvc.perform(post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Token is not valid. Please, try login again"));
    }
}