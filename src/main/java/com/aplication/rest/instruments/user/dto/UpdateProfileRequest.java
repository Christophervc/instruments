package com.aplication.rest.instruments.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank(message = "First name is mandatory")
        String firstName,
        @NotBlank(message = "Last name is mandatory")
        String lastName,
        @Size(min = 9, max = 13, message = "Phone number must be at least 9 digits")
        String phone
) {
}
