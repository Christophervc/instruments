package com.aplication.rest.instruments.user.dto;

import com.aplication.rest.instruments.user.enums.Role;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleRequest(
        @NotNull(message = "New role is required")
        Role role
) {
}
