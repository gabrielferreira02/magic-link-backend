package com.gabrielferreira02.MagicLink.dto;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDTO(@NotBlank String username,@NotBlank String email) {
}
