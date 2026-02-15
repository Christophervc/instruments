package com.aplication.rest.instruments.manufacturer;

import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ManufacturerServiceImplement implements IManufacturerService {
    @Autowired
    private ManufacturerRepository manufacturerRepository;
    @Autowired
    private ManufacturerMapper manufacturerMapper;

    @Override
    public Result<List<ManufacturerDTO>> findAll() {
        try {
            List<Manufacturer> manufacturers = manufacturerRepository.findAll();
            if (((manufacturerRepository.findAll()).isEmpty())) return Result.success(List.of());
            List<ManufacturerDTO> manufacturerDTO = manufacturers.stream().map(manufacturer -> manufacturerMapper.toDTO(manufacturer)).toList();
            return Result.success(manufacturerDTO);
        } catch (Exception e) {
            return Result.isFailure(new ApiError("DATABASE ERROR", "Error retrieving manufacturers"));
        }
    }

    @Override
    public Result<ManufacturerDTO> findById(UUID id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found a manufacturer with id: " + id));
        ManufacturerDTO manufacturerDTO = manufacturerMapper.toDTO(manufacturer);
        return Result.success(manufacturerDTO);
    }

    @Override
    public Result<List<ManufacturerDTO>> findByName(String name) {
        List<Manufacturer> existingManufacturers = manufacturerRepository.findByNameContainingIgnoreCase(name);
        List<ManufacturerDTO> manufacturerDTOS = existingManufacturers.stream().map(manufacturer -> manufacturerMapper.toDTO(manufacturer)).toList();
        return Result.success(manufacturerDTOS);
    }

    @Override
    public Result<ManufacturerDTO> save(ManufacturerDTO manufacturerDTO) {
      Manufacturer manufacturer = manufacturerMapper.toEntity(manufacturerDTO);
      Manufacturer savedManufacturer = manufacturerRepository.save(manufacturer);
      ManufacturerDTO savedManufacturerDTO = manufacturerMapper.toDTO(savedManufacturer);
      return Result.success(savedManufacturerDTO);
    }

    @Override
    public Result<ManufacturerDTO> update(UUID id, ManufacturerDTO manufacturerDTO) {
        Manufacturer existingManufacturer = manufacturerRepository.findById(id).orElseThrow(()-> new NotFoundException("Not found a manufacturer with id: " + id));
        manufacturerMapper.updateManufacturerFromDto(manufacturerDTO, existingManufacturer);
        Manufacturer savedManufacturer = manufacturerRepository.save(existingManufacturer);
        return Result.success(manufacturerMapper.toDTO(savedManufacturer));
    }

    @Override
    public Result<ManufacturerDTO> deleteById(UUID id) {
        Manufacturer manufacturer = manufacturerRepository.findById(id).orElseThrow(()-> new NotFoundException("Not found manufacturer with id: " +id));
        ManufacturerDTO manufacturerDTO = manufacturerMapper.toDTO(manufacturer);
        manufacturerRepository.deleteById(id);
        return Result.success(manufacturerDTO);
    }
}
