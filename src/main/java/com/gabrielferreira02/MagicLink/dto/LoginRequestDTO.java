package com.gabrielferreira02.MagicLink.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(@NotBlank String email) {
}
