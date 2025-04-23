package com.grid.dynamicstore.service;

import com.grid.dynamicstore.dto.OrderDto;

import java.util.List;

public interface OrderService {
    OrderDto checkout(String sessionId);
    List<OrderDto> getOrders(String sessionId);
    void cancelOrder(Long orderId, String sessionId);
}
