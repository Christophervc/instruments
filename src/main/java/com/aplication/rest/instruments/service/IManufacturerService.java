package com.aplication.rest.instruments.service;

import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.entities.Manufacturer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IManufacturerService {

    Result<List<ManufacturerDTO>> findAll();

    Result<Optional<ManufacturerDTO>> findById(UUID id);

    Result<ManufacturerDTO> save(ManufacturerDTO manufacturerDTO);

    Result<ManufacturerDTO> update(UUID id, ManufacturerDTO manufacturerDTO);

    Result<ManufacturerDTO> deleteById(UUID id);
}
