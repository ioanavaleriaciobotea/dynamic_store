package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.ProductAddDto;
import com.grid.dynamicstore.dto.ProductDto;
import com.grid.dynamicstore.dto.ProductUpdateDto;

import java.util.List;

public interface ProductService {
    ProductDto addProduct(ProductAddDto dto);
    List<ProductDto> getAllProducts();
    ProductDto updateProductByTitle(String existingTitle, ProductUpdateDto dto);
    void deleteByTitle(String title);
}
