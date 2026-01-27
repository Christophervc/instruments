package com.aplication.rest.instruments.manufacturer;

import com.aplication.rest.instruments.manufacturer.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
            List<Manufacturer> manufacturers = (List<Manufacturer>) manufacturerRepository.findAll();

            if (((List<Manufacturer>) manufacturerRepository.findAll()).isEmpty()) return Result.success(List.of());
            List<ManufacturerDTO> manufaturerDTO = manufacturers.stream().map(manufacturer -> manufacturerMapper.toDTO(manufacturer)).toList();
            return Result.success(manufaturerDTO);
        } catch (Exception e) {
            return Result.isFailure(new ApiError("DATABASE ERROR", "Error retrieving manufacturers"));
        }
    }

    @Override
    public Result<Optional<ManufacturerDTO>> findById(UUID id) {
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
