package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.CartDto;

public interface CartService {
    CartDto getCart(String sessionId);
    void clearCart(String sessionId);
}
