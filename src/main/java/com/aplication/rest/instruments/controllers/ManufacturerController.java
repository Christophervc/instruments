package com.aplication.rest.instruments.controllers;


import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.service.IManufacturerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/manufacturers")

public class ManufacturerController {

    @Autowired
    private IManufacturerService manufacturerService;

    @GetMapping()
    public ResponseEntity<Result<List<ManufacturerDTO>>> findAll(){
        return ResponseEntity.ok(manufacturerService.findAll());
    }
    @GetMapping ("/{id}")
    public ResponseEntity<Result<Optional<ManufacturerDTO>>> findById(@PathVariable UUID id){
        return ResponseEntity.ok(manufacturerService.findById(id));
    }
    @PostMapping()
    public ResponseEntity<Result<ManufacturerDTO>> save(@Valid @RequestBody ManufacturerDTO manufacturerDTO) throws URISyntaxException {
        Result<ManufacturerDTO> result = manufacturerService.save(manufacturerDTO);
        return ResponseEntity.created(new URI("/api/v1/manufacturers/" + result.data().getId()))
                .body(result);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Result<ManufacturerDTO>> update(@PathVariable UUID id, @Valid @RequestBody ManufacturerDTO manufacturerDTO) {
        return ResponseEntity.ok(manufacturerService.update(id, manufacturerDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<ManufacturerDTO>> deleteById(@PathVariable UUID id){
        return ResponseEntity.ok(manufacturerService.deleteById(id));
    }
}
