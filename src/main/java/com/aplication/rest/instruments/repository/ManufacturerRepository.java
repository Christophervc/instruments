package com.aplication.rest.instruments.repository;

import com.aplication.rest.instruments.entities.Manufacturer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends CrudRepository<Manufacturer, Long> {
}
