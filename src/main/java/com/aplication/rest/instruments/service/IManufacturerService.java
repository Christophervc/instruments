package com.aplication.rest.instruments.service;

import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.entities.Manufacturer;

import java.util.List;
import java.util.Optional;

public interface IManufacturerService {

    Result<List<ManufacturerDTO>> findAll();

    Result<Optional<ManufacturerDTO>> findById(Long id);

    Result<ManufacturerDTO> save(ManufacturerDTO manufacturerDTO);

    Result<ManufacturerDTO> update(Long id, ManufacturerDTO manufacturerDTO);

    Result<ManufacturerDTO> deleteById(Long id);
}
