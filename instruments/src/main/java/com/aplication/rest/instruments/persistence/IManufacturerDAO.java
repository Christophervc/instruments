package com.aplication.rest.instruments.persistence;

import com.aplication.rest.instruments.entities.Manufacturer;


import java.util.List;
import java.util.Optional;

public interface IManufacturerDAO {

    List<Manufacturer> findAll();

    Optional<Manufacturer> findById(Long id);

    void save(Manufacturer manufacturer);

    void deleteById(Long id);

}
