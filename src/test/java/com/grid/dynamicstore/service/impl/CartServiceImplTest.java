package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.dto.CartItemDto;
import com.grid.dynamicstore.dto.CartResponseDto;
import com.grid.dynamicstore.exception.EntityNotFoundException;
import com.grid.dynamicstore.exception.QuantityNotInStock;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Product product;
    private String sessionId;

    @BeforeEach
    void setUp() {
        sessionId = "session123";
        product = new Product();
        product.setId(1L);
        product.setTitle("Test Product");
        product.setAvailable(10);
        product.setPrice(BigDecimal.valueOf(100));
    }

    @Test
    void testAddProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        cartService.addProduct(sessionId, 1L, 2);

        CartDto cart = cartService.getCart(sessionId);
        assertEquals(2, cart.getProductQuantities().get(1L));
    }

    @Test
    void testAddProduct_NotEnoughStock() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(QuantityNotInStock.class, () -> cartService.addProduct(sessionId, 1L, 20));
    }

    @Test
    void testAddProduct_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.addProduct(sessionId, 1L, 2));
    }

    @Test
    void testViewCart_EmptyCart() {
        CartResponseDto response = cartService.viewCart(sessionId);
        assertTrue(response.getItems().isEmpty());
    }

    @Test
    void testViewCart_FullCart() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addProduct(sessionId, 1L, 3);

        CartResponseDto response = cartService.viewCart(sessionId);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());

        CartItemDto item = response.getItems().get(0);
        assertEquals(1L, item.getProductId());
        assertEquals("Test Product", item.getTitle());
        assertEquals(3, item.getQuantity());
        assertEquals(BigDecimal.valueOf(300), item.getSubtotal());
        assertEquals(1, item.getOrdinal());

        assertEquals(BigDecimal.valueOf(300), response.getTotal());
    }

    @Test
    void testUpdateProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addProduct(sessionId, 1L, 2);

        CartItemDto updatedItem = cartService.updateProduct(sessionId, 1L, 5);

        assertEquals(5, updatedItem.getQuantity());
        assertEquals(BigDecimal.valueOf(500), updatedItem.getSubtotal());
    }

    @Test
    void testRemoveProduct_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        cartService.addProduct(sessionId, 1L, 1);

        cartService.removeProduct(sessionId, 1L);
        assertFalse(cartService.getCart(sessionId).getProductQuantities().containsKey(1L));
    }

    @Test
    void testRemoveProduct_NotInCart() {
        assertThrows(RuntimeException.class, () -> cartService.removeProduct(sessionId, 99L));
    }
}
