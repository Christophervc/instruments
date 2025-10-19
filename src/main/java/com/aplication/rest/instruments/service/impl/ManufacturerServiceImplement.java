package com.aplication.rest.instruments.service.impl;

import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.ApiError;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.core.exceptions.NotFoundException;
import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.entities.Product;
import com.aplication.rest.instruments.persistence.IManufacturerDAO;
import com.aplication.rest.instruments.persistence.IProductDAO;
import com.aplication.rest.instruments.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ManufacturerServiceImplement implements IManufacturerService {
    @Autowired
    private IManufacturerDAO manufacturerDAO;

    @Override
    public Result<List<ManufacturerDTO>> findAll() {
        try {
            List<Manufacturer> manufacturers = manufacturerDAO.findAll();

            if (manufacturerDAO.findAll().isEmpty()) return Result.success(List.of());
            List<ManufacturerDTO> manufaturerDTO = manufacturers.stream().map(manufacturer -> ManufacturerDTO.builder()
            .id(manufacturer.getId())
            .name(manufacturer.getName())
            .build()).toList();
            return Result.success(manufaturerDTO);

        } catch (Exception e) {
            ApiError error = new ApiError("DATABASE ERROR", "Error retrieving manufacturers");
            return Result.isFailure(error);
        }

    }

    @Override
    public Result<Optional<ManufacturerDTO>> findById(Long id) {
        try {
            Optional<Manufacturer> optionalManufacturer = manufacturerDAO.findById(id);
            if (optionalManufacturer.isEmpty()) return Result.success(Optional.empty());
            Manufacturer manufacturer = optionalManufacturer.get();
            ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder()
            .id(manufacturer.getId())
            .name(manufacturer.getName())
            .build();
            return Result.success(Optional.of(manufacturerDTO));
        } catch (Exception e) {
            ApiError error = new ApiError("DATABASE ERROR", "Error retrieving a manufacturer");
            return Result.isFailure(error);
        }
    }

    @Override
    public Result<ManufacturerDTO> save(ManufacturerDTO manufacturerDTO) {
      if (manufacturerDTO.getName().isBlank()){
        ApiError error = new ApiError("BAD REQUEST", "Name is required");
        return Result.isFailure(error);
      }
      Manufacturer manufacturer = Manufacturer.builder().name(manufacturerDTO.getName()).build();
      Manufacturer savedManufacturer = manufacturerDAO.save(manufacturer);
      return Result.success(ManufacturerDTO.builder().id(savedManufacturer.getId()).name(savedManufacturer.getName()).build());
    }

    @Override
    public Result<ManufacturerDTO> deleteById(Long id) {
        Manufacturer manufacturer = manufacturerDAO.findById(id).orElseThrow(()-> new NotFoundException("Not found manufacturer with id: " +id));
        ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder().id(manufacturer.getId()).name(manufacturer.getName()).build();
        manufacturerDAO.deleteById(id);
        return Result.success(manufacturerDTO);
    }

    /*
    @Override
    public List<Manufacturer> findAll() {
        return manufacturerDAO.findAll();
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        return manufacturerDAO.findById(id);
    }

    @Override
    public void save(Manufacturer manufacturer) {
        manufacturerDAO.save(manufacturer);
    }

    @Override
    public void deleteById(Long id) {
        manufacturerDAO.deleteById(id);
    }
    */
}
