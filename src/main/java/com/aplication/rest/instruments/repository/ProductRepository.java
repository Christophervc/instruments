package com.aplication.rest.instruments.repository;


import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.entities.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends CrudRepository<Product, UUID> {

    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> findByManufacturer(Manufacturer manufacturer);
    List<Product> findAllByOrderByNameAsc();
    List<Product> findAllByOrderByNameDesc();
}
