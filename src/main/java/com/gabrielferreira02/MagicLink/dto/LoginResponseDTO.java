package com.gabrielferreira02.MagicLink.dto;

import java.time.Instant;

public record LoginResponseDTO(String token, Instant expiration) {
}
