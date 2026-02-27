package com.aplication.rest.instruments.product;

import com.aplication.rest.instruments.product.dto.ProductDTO;
import com.aplication.rest.instruments.core.error_handling.Result;
import com.aplication.rest.instruments.product.dto.ProductSearchCriteria;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping()
    @Operation(summary = "Get all products", description = "filter by name, price, manufacturer and type ")
    @SecurityRequirements()
    public ResponseEntity<Result<Page<ProductDTO>>> findAll(
            @ParameterObject @PageableDefault(size = 10, page = 0, sort = "name") Pageable pageable,
            @ParameterObject ProductSearchCriteria criteria) {
        return ResponseEntity.ok(productService.findAll(pageable, criteria));
    }

    @Operation(summary = "Get a product details", description = "Get a product details")
    @SecurityRequirements()
    @GetMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Operation(summary = "Get a product details by sku", description = "Get a product details by stock keeping unit")
    @SecurityRequirements()
    @GetMapping("sku/{sku}")
    public ResponseEntity<Result<ProductDTO>> findBySku(@PathVariable String sku) {
        return ResponseEntity.ok(productService.findBySku(sku));
    }

    @Operation(summary = "Export all products catalog to excel", description = "Export all products catalog to excel sheet, requires ADMIN or STAFF role")
    @GetMapping("/export/excel")
    public void exportToExcel(HttpServletResponse response) {
        productService.exportProductsToExcel(response);
    }

    @Operation(summary = "Create a new product", description = "adds a new musical instrument to the inventory, requires STAFF or ADMIN role")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result<ProductDTO>> save(@RequestPart("product") @Valid ProductDTO productDTO,
                                                   @RequestPart(value = "file", required = false) MultipartFile file) {
        Result<ProductDTO> result = productService.save(productDTO, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "Upload a product image", description = "Upload a product image for an existing product, requires STAFF or ADMIN role")
    @PostMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Result<ProductDTO>> uploadProductImage(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        Result<ProductDTO> result = productService.uploadImage(id, file);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Update a product", description = "Update a product, requires STAFF or ADMIN role")
    @PutMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> update(@PathVariable UUID id,
            @Valid @RequestBody ProductDTO productDTO) {
        Result<ProductDTO> result = productService.update(id, productDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Delete a product", description = "soft deletes a product marks it as inactive, requires STAFF or ADMIN role")
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<ProductDTO>> deleteById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.deleteById(id));
    }
}
