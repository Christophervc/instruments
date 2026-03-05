package com.aplication.rest.instruments.manufacturer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.io.Serializable;
import java.util.UUID;


@Builder
public record ManufacturerDTO(
        UUID id,
        @NotBlank(message = "{manufacturer.name.required}")
        String name
) implements Serializable {}
