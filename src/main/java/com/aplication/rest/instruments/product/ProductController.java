package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping()
    @Operation(summary = "Get all products", description = "filter by name, price, manufacturer and type ")
    public ResponseEntity<Result<Page<ProductDTO>>> findAll(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable,
            @ParameterObject ProductSearchCriteria criteria) {
        return ResponseEntity.ok(productService.findAll(pageable, criteria));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("sku/{sku}")
    public ResponseEntity<Result<ProductDTO>> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        productService.exportProductsToExcel(response);
    }

    @PostMapping()
    public ResponseEntity<Result<ProductDTO>> save(@Valid @RequestBody ProductDTO productDTO) throws URISyntaxException {
        Result<ProductDTO> result = productService.save(productDTO);
        return ResponseEntity.created(new URI("/api/v1/products/" + result.data().getId()))
                .body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> update(@PathVariable UUID id, @Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.update(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> deleteById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteById(id));
    }
}
