package com.gabrielferreira02.MagicLink.service;

public interface EmailService {
    void sendEmail(String email, String username, String token);
}
