package com.aplication.rest.instruments.controllers;

import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.service.IProductService;
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
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping()
    public ResponseEntity<Result<List<ProductDTO>>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<Optional<ProductDTO>>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping()
    public ResponseEntity<Result<ProductDTO>> save(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException{
        Result<ProductDTO> result = productService.save(productDTO);
        return ResponseEntity.created(new URI("/api/v1/products/" + result.data().getId()))
                .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> deleteById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> update(@PathVariable UUID id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }
}
