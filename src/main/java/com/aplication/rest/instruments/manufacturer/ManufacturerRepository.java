package com.aplication.rest.instruments.manufacturer;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ManufacturerRepository extends CrudRepository<Manufacturer, UUID> {
}
