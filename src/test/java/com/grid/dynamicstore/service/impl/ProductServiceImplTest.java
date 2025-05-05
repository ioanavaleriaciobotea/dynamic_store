package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.ProductAddDto;
import com.grid.dynamicstore.dto.ProductDto;
import com.grid.dynamicstore.dto.ProductUpdateDto;
import com.grid.dynamicstore.exception.DuplicateEntityException;
import com.grid.dynamicstore.exception.EntityNotFoundException;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void addProduct_success() {
        ProductAddDto dto = new ProductAddDto();
        dto.setTitle("Drill");
        dto.setAvailable(10);
        dto.setPrice(BigDecimal.valueOf(99.99));

        when(productRepository.findByTitle("Drill")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto result = productService.addProduct(dto);

        assertEquals("Drill", result.getTitle());
        assertEquals(10, result.getAvailable());
        assertEquals(BigDecimal.valueOf(99.99), result.getPrice());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void addProduct_duplicate_throwsException() {
        ProductAddDto dto = new ProductAddDto();
        dto.setTitle("Drill");

        when(productRepository.findByTitle("Drill")).thenReturn(Optional.of(new Product()));

        assertThrows(DuplicateEntityException.class, () -> productService.addProduct(dto));
    }

    @Test
    void updateProductByTitle_success() {
        Product existing = new Product();
        existing.setTitle("Old");
        existing.setAvailable(5);
        existing.setPrice(BigDecimal.valueOf(50));

        ProductUpdateDto dto = new ProductUpdateDto();
        dto.setTitle("New");
        dto.setAvailable(15);
        dto.setPrice(BigDecimal.valueOf(150));

        when(productRepository.findByTitle("Old")).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDto result = productService.updateProductByTitle("Old", dto);

        assertEquals("New", result.getTitle());
        assertEquals(15, result.getAvailable());
        assertEquals(BigDecimal.valueOf(150), result.getPrice());
    }

    @Test
    void updateProductByTitle_notFound_throwsException() {
        when(productRepository.findByTitle("Missing")).thenReturn(Optional.empty());

        ProductUpdateDto dto = new ProductUpdateDto();
        assertThrows(EntityNotFoundException.class, () -> productService.updateProductByTitle("Missing", dto));
    }

    @Test
    void deleteByTitle_success() {
        Product product = new Product();
        when(productRepository.findByTitle("Delete"))
                .thenReturn(Optional.of(product));

        productService.deleteByTitle("Delete");

        verify(productRepository).delete(product);
    }

    @Test
    void deleteByTitle_notFound_throwsException() {
        when(productRepository.findByTitle("Missing"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.deleteByTitle("Missing"));
    }

    @Test
    void getAllProducts_returnsList() {
        Product product = new Product();
        product.setId(1L);
        product.setTitle("Drill");
        product.setAvailable(5);
        product.setPrice(BigDecimal.valueOf(59.99));

        when(productRepository.findAll()).thenReturn(List.of(product));

        List<ProductDto> result = productService.getAllProducts();

        assertEquals(1, result.size());
        assertEquals("Drill", result.get(0).getTitle());
    }
}
