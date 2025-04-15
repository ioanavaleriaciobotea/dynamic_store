package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.ProductAddRequestDto;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import com.grid.dynamicstore.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(ProductAddRequestDto dto) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setAvailable(dto.getAvailable());
        product.setPrice(dto.getPrice());

        return productRepository.save(product);
    }
}
