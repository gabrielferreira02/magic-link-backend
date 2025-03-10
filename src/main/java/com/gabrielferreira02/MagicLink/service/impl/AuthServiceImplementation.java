package com.gabrielferreira02.MagicLink.service.impl;

import com.gabrielferreira02.MagicLink.dto.LoginRequestDTO;
import com.gabrielferreira02.MagicLink.dto.RegisterRequestDTO;
import com.gabrielferreira02.MagicLink.dto.ValidateResponseDTO;
import com.gabrielferreira02.MagicLink.entity.UserEntity;
import com.gabrielferreira02.MagicLink.repository.UserRepository;
import com.gabrielferreira02.MagicLink.service.AuthService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthServiceImplementation implements AuthService {

    private final UserRepository userRepository;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void createUser(RegisterRequestDTO request) {
        if(userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email address already exists");
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
            throw new RuntimeException("Token not found");
        }

        Date tokenExpiresDate = user.get().getExpirationTime();
        Date now = new Date();

        if(tokenExpiresDate.before(now)) {
            throw new RuntimeException("Token is not valid. Please, try login again");
        }

        return new ValidateResponseDTO(user.get().getUsername(), true);
    }

    public void login(LoginRequestDTO request) {
        Optional<UserEntity> user = userRepository.findByEmail(request.email());

        if(user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        user.get().setValidationToken(UUID.randomUUID().toString());
        user.get().setExpirationTime(new Date(System.currentTimeMillis() + (10*60*1000)));
        userRepository.save(user.get());
        sendEmail(request.email(), user.get().getUsername(), user.get().getValidationToken());
    }

    private void sendEmail(String email, String username, String token) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(this.username);
            helper.setTo(email);
            helper.setSubject("Acesso liberado para o Dashboard!");

            ClassPathResource classPathResource = new ClassPathResource("email.html");
            String template = new String(classPathResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            template = template.replace("#{name}", username);
            template = template.replace("#{token}", token);
            helper.setText(template, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
