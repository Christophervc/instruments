package com.aplication.rest.instruments.persistence.impl;

import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.persistence.IManufacturerDAO;
import com.aplication.rest.instruments.repository.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ManufacturerDAOImplement implements IManufacturerDAO {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Override
    public List<Manufacturer> findAll() {
        return (List<Manufacturer>) manufacturerRepository.findAll();
    }

    @Override
    public Optional<Manufacturer> findById(Long id) {
        return manufacturerRepository.findById(id);
    }

    @Override
    public void save(Manufacturer manufacturer) {
        manufacturerRepository.save(manufacturer);
    }

    @Override
    public void deleteById(Long id) {
        manufacturerRepository.deleteById(id);
    }
}
