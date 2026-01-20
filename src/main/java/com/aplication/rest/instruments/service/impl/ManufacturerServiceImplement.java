package com.aplication.rest.instruments.service.impl;

import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.mapper.ManufacturerMapper;
import com.aplication.rest.instruments.persistence.IManufacturerDAO;
import com.aplication.rest.instruments.repository.ManufacturerRepository;
import com.aplication.rest.instruments.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerServiceImplement implements IManufacturerService {
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private ManufacturerMapper manufacturerMapper;

    @Override
    public Result<List<ManufacturerDTO>> findAll() {
        try {
            List<Manufacturer> manufacturers = (List<Manufacturer>) manufacturerRepository.findAll();

            if (((List<Manufacturer>) manufacturerRepository.findAll()).isEmpty()) return Result.success(List.of());
            List<ManufacturerDTO> manufaturerDTO = manufacturers.stream().map(manufacturer -> manufacturerMapper.toDTO(manufacturer)).toList();
            return Result.success(manufaturerDTO);
        } catch (Exception e) {
            return Result.isFailure(new ApiError("DATABASE ERROR", "Error retrieving manufacturers"));
        }
    }

    @Override
    public Result<Optional<ManufacturerDTO>> findById(Long id) {
        try {
            Optional<Manufacturer> optionalManufacturer = manufacturerRepository.findById(id);
            if (optionalManufacturer.isEmpty()) return Result.success(Optional.empty());
            Manufacturer manufacturer = optionalManufacturer.get();
            ManufacturerDTO manufacturerDTO = manufacturerMapper.toDTO(manufacturer);
            return Result.success(Optional.of(manufacturerDTO));
        } catch (Exception e) {
            return Result.isFailure(new ApiError("DATABASE ERROR", "Error retrieving a manufacturer"));
        }
    }

    @Override
    public Result<ManufacturerDTO> save(ManufacturerDTO manufacturerDTO) {
      if (manufacturerDTO.getName().isBlank()){
        return Result.isFailure(new ApiError("BAD REQUEST", "Name is required"));
      }

      Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDTO);
      Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
      ManufacturerDTO savedManufacturerDTO = manufacturerMapper.toDTO(savedManufacturer);
      return Result.success(savedManufacturerDTO);
    }

    @Override
    public Result<ManufacturerDTO> deleteById(Long id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(()-> new NotFoundException("Not found manufacturer with id: " +id));
        ManufacturerDTO manufacturerDTO = manufacturerMapper.toDTO(manufacturer);
        manufacturerRepository.deleteById(id);
        return Result.success(manufacturerDTO);
    }
}
