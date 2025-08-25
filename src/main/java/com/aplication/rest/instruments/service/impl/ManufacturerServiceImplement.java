package com.aplication.rest.instruments.service.impl;

import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.persistence.IManufacturerDAO;
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
}
