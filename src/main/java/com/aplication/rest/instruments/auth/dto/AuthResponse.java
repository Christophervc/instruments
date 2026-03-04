package com.aplication.rest.instruments.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken,
                           String refreshToken,
                           String message) {
}
