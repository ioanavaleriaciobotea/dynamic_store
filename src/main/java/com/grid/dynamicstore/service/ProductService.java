package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.ProductAddRequestDto;
import com.grid.dynamicstore.model.Product;

public interface ProductService {
    Product addProduct(ProductAddRequestDto dto);
}
