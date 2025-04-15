package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.service.CartService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CartServiceImpl implements CartService {
    private final Map<String, CartDto> carts = new ConcurrentHashMap<>();

    @Override
    public CartDto getCart(String sessionId) {
        return carts.computeIfAbsent(sessionId, id -> new CartDto());
    }

    @Override
    public void clearCart(String sessionId) {
        carts.remove(sessionId);
    }
}
