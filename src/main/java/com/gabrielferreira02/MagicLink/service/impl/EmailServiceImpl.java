package com.gabrielferreira02.MagicLink.service.impl;

import com.gabrielferreira02.MagicLink.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    public void sendEmail(String email, String username, String token) {
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
