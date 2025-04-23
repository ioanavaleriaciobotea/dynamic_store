package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.ProductAddDto;
import com.grid.dynamicstore.dto.ProductDto;
import com.grid.dynamicstore.dto.ProductUpdateDto;
import com.grid.dynamicstore.exception.DuplicateEntityException;
import com.grid.dynamicstore.exception.EntityNotFoundException;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import com.grid.dynamicstore.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto addProduct(ProductAddDto dto) {

        if (productRepository.findByTitle(dto.getTitle()).isPresent()) {
            throw new DuplicateEntityException("Product already exists!");
        }

        Product product = dto.convertToEntity();

        return new ProductDto(productRepository.save(product));
    }

    @Override
    public ProductDto updateProductByTitle(String existingTitle, ProductUpdateDto dto) {

        Product existingProduct = productRepository.findByTitle(existingTitle)
                .orElseThrow(() -> new EntityNotFoundException("Product not found!"));

        Optional.ofNullable(dto.getTitle()).ifPresent(existingProduct::setTitle);
        Optional.ofNullable(dto.getAvailable()).ifPresent(existingProduct::setAvailable);
        Optional.ofNullable(dto.getPrice()).ifPresent(existingProduct::setPrice);

        return new ProductDto(productRepository.save(existingProduct));
    }

    @Override
    public void deleteByTitle(String title) {

        Product product = productRepository.findByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("Product with not found!"));

        productRepository.delete(product);
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(ProductDto::new).toList();
    }
}
