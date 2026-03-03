package com.aplication.rest.instruments.order.dto;

import java.util.UUID;

public record CustomerInfoDTO(
        UUID id,
        String firstName,
        String lastName,
        String email,
        String dni,
        String phone,
        Boolean active
) {}
