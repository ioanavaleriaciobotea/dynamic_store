package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.ProductAddRequestDto;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductAddRequestDto dto) {
        Product product = productService.addProduct(dto);
        return ResponseEntity.ok(product);
    }
}
