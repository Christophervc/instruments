package com.aplication.rest.instruments.controllers;


import com.aplication.rest.instruments.controllers.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.entities.Product;
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

    /*
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ProductDTO productDTO) throws URISyntaxException {

        if (productDTO.getName().isBlank() || productDTO.getType().isBlank() || productDTO.getDescription().isBlank() || productDTO.getPrice() == null || productDTO.getManufacturer() == null) {
            return ResponseEntity.badRequest().body("Please enter the following fields: name, type, description, price, manufacturer");
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .type(productDTO.getType())
                .description(productDTO.getDescription())
                .price(productDTO.getPrice())
                .manufacturer(productDTO.getManufacturer())
                .build();

        productService.save(productDTO);
        return ResponseEntity.created(new URI("/api/products/save")).build();
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ProductDTO productDTO) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            product.setName(productDTO.getName());
            product.setType(productDTO.getType());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setManufacturer(productDTO.getManufacturer());
            productService.save(product);
            return ResponseEntity.ok("Updated");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        if (id != null) {
            productService.deleteById(id);
            return ResponseEntity.ok("Product Deleted");
        }
        return ResponseEntity.badRequest().build();
    }

    /*
    @GetMapping("/find/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Optional<Product> ProductOptional = productService.findById(id);
        if (ProductOptional.isPresent()) {
            Product product = ProductOptional.get();
            ProductDTO productDTO = ProductDTO.builder()
                    .id(product.getId())
                    .name(product.getName())
                    .type(product.getType())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .manufacturer(product.getManufacturer())
                    .build();
            return ResponseEntity.ok(productDTO);
        }
        return ResponseEntity.notFound().build();
    }


    @GetMapping("/sortedByNameAsc")
    public ResponseEntity<?> findAllSortedByNameAsc() {
        List<ProductDTO> productDTOList = productService.findAllSortedByNameAsc()
                .stream()
                .map(product -> ProductDTO.builder().id(product.getId())
                        .name(product.getName()).type(product.getType()).description(product.getDescription()).price(product.getPrice()).manufacturer(product.getManufacturer()).build()).toList();
        return ResponseEntity.ok(productDTOList);
    }
    */
}
