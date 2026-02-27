package com.aplication.rest.instruments.auth.dto;

import java.util.UUID;

public record UserProfileDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String dni,
        String phone,
        String role
) {
}
