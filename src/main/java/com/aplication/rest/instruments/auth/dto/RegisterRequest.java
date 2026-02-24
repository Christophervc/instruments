package com.aplication.rest.instruments.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is mandatory") String firstName,
        @NotBlank(message = "Last name is mandatory") String lastName,
        @Email(message = "Invalid email format")
        @NotBlank(message = "email is mandatory") String email,
        @NotBlank(message = "Password is mandatory")
        @Size(min = 6, message = "Password must be at least 6 characters long") String password,
        @NotBlank(message = "DNI is mandatory")
        @Size(min = 8, max = 8, message = "DNI must have exactly 8 characters") String dni,
        String phone
) {
}


