package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.ProductAddDto;
import com.grid.dynamicstore.dto.ProductDto;
import com.grid.dynamicstore.dto.ProductUpdateDto;
import com.grid.dynamicstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@EnableMethodSecurity(prePostEnabled = true)
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> addProduct(@Valid @RequestBody ProductAddDto dto) {

        ProductDto product = productService.addProduct(dto);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDto> updateByTitle(@PathVariable String title,
                                                    @RequestBody ProductUpdateDto dto) {

        ProductDto updated = productService.updateProductByTitle(title, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{title}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteByTitle(@PathVariable String title) {

        productService.deleteByTitle(title);
        return ResponseEntity.ok("Product deleted successfully.");
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {

        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
}
