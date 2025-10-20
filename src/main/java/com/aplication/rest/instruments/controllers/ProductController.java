package com.aplication.rest.instruments.controllers;

import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/all")
    public ResponseEntity<Result<List<ProductDTO>>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/find/{id}")
    public ResponseEntity<Result<Optional<ProductDTO>>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PostMapping("/save")
    public ResponseEntity<Result<ProductDTO>> save(@RequestBody ProductDTO productDTO) throws URISyntaxException{
        ResponseEntity.ok(productService.save(productDTO));
        return ResponseEntity.created(new URI("/api/products/save")).build();
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<Result<ProductDTO>> deleteById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.deleteById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Result<ProductDTO>> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }
}
