package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.dto.CartItemDto;
import com.grid.dynamicstore.dto.CartResponseDto;

public interface CartService {
    void addProduct(String sessionId, Long productId, int quantity);
    CartResponseDto viewCart(String sessionId);
    void removeProduct(String sessionId, Long productId);
    CartItemDto updateProduct(String sessionId, Long productId, int quantity);
    CartDto getCart(String sessionId);
    void clearCart(String sessionId);
}
