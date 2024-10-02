package com.aplication.rest.instruments.controllers;


import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.entities.Manufacturer;
import com.aplication.rest.instruments.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/manufacturers")

public class ManufacturerController {

    @Autowired
    private IManufacturerService manufacturerService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<ManufacturerDTO> manufacturerList = manufacturerService.findAll()
                .stream().map(manufacturer -> ManufacturerDTO.builder()
                        .id(manufacturer.getId())
                        .name(manufacturer.getName())
                        .productList(manufacturer.getProductList())
                        .build()
                ).toList();
        return ResponseEntity.ok(manufacturerList);
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Manufacturer> manufacturerOptional = manufacturerService.findById(id);
        if (manufacturerOptional.isPresent()) {
            Manufacturer manufacturer = manufacturerOptional.get();
            ManufacturerDTO manufacturerDTO = ManufacturerDTO.builder()
                    .id(manufacturer.getId())
                    .name(manufacturer.getName())
                    .productList(manufacturer.getProductList())
                    .build();
            return ResponseEntity.ok(manufacturerDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ManufacturerDTO manufacturerDTO) throws URISyntaxException {
        if (manufacturerDTO.getName().isBlank()) {
            return ResponseEntity.badRequest().body("Please enter a name");
        }
        manufacturerService.save(Manufacturer.builder()
                .name(manufacturerDTO.getName())
                .build());
        return ResponseEntity.created(new URI("/api/manufacturers/save")).build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ManufacturerDTO manufacturerDTO) {
        Optional<Manufacturer> manufacturerOptional = manufacturerService.findById(id);
        if (manufacturerOptional.isPresent()) {
            Manufacturer manufacturer = manufacturerOptional.get();
            manufacturer.setName(manufacturerDTO.getName());
            manufacturerService.save(manufacturer);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (id != null) {
            manufacturerService.deleteById(id);
            return ResponseEntity.ok("Deleted");
        }
        return ResponseEntity.notFound().build();
    }
}
