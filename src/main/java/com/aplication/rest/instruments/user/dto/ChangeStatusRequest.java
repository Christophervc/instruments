package com.aplication.rest.instruments.user.dto;

import jakarta.validation.constraints.NotNull;

public record ChangeStatusRequest(
        @NotNull(message = "Status (active) is mandatory")
        Boolean active) {}
