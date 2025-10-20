package com.aplication.rest.instruments.controllers;


import com.aplication.rest.instruments.controllers.dto.ManufacturerDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.service.IManufacturerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/manufacturers")

public class ManufacturerController {

    @Autowired
    private IManufacturerService manufacturerService;

    @GetMapping("/all")
    public ResponseEntity<Result<List<ManufacturerDTO>>> findAll(){
        return ResponseEntity.ok(manufacturerService.findAll());
    }
    @GetMapping ("find/{id}")
    public ResponseEntity<Result<Optional<ManufacturerDTO>>> findById(@PathVariable Long id){
        return ResponseEntity.ok(manufacturerService.findById(id));
    }
    @PostMapping("/save")
    public ResponseEntity<Result<ManufacturerDTO>> save(@RequestBody ManufacturerDTO manufacturerDTO){
        return ResponseEntity.ok(manufacturerService.save(manufacturerDTO));
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Result<ManufacturerDTO>> deleteById(@PathVariable Long id){
        return ResponseEntity.ok(manufacturerService.deleteById(id));
    }
}
