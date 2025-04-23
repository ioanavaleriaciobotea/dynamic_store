package com.grid.dynamicstore.controller;

import com.grid.dynamicstore.dto.OrderDto;
import com.grid.dynamicstore.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<OrderDto> checkout(HttpSession session) {
        OrderDto order = orderService.checkout(session.getId());
        return ResponseEntity.ok(order);
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(HttpSession session) {
        List<OrderDto> orders = orderService.getOrders(session.getId());
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, HttpSession session) {
        orderService.cancelOrder(orderId, session.getId());
        return ResponseEntity.ok("Order cancelled and stock restored.");
    }
}
