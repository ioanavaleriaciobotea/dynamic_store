package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.dto.OrderDto;
import com.grid.dynamicstore.exception.EntityNotFoundException;
import com.grid.dynamicstore.exception.QuantityNotInStock;
import com.grid.dynamicstore.model.*;
import com.grid.dynamicstore.repository.OrderRepository;
import com.grid.dynamicstore.repository.ProductRepository;
import com.grid.dynamicstore.repository.UserRepository;
import com.grid.dynamicstore.service.CartService;
import com.grid.dynamicstore.service.OrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private final CartService cartService;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderServiceImpl(CartService cartService,
                            ProductRepository productRepository,
                            OrderRepository orderRepository,
                            UserRepository userRepository) {
        this.cartService = cartService;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @Override
    public OrderDto checkout(String sessionId) {
        CartDto cart = cartService.getCart(sessionId);

        if (cart.isEmpty()) {
            throw new IllegalStateException("Cart is empty.");
        }

        // Get authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        Set<OrderItem> items = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : cart.getProductQuantities().entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found."));

            if (product.getAvailable() < quantity) {
                throw new QuantityNotInStock("Not enough stock for product: " + product.getTitle());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(quantity));

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPriceEach(product.getPrice());
            item.setTotalPrice(itemTotal);
            item.setOrder(order);

            items.add(item);
            total = total.add(itemTotal);

            // Deduct from stock
            product.setAvailable(product.getAvailable() - quantity);
        }

        order.setTotal(total);
        order.setOrderItems(items);

        // Save everything
        orderRepository.save(order);

        // Clear cart
        cartService.clearCart(sessionId);

        return new OrderDto(order);
    }

    @Override
    public List<OrderDto> getOrders(String sessionId) {
        return orderRepository.findAll().stream().map(this::convertToDto).toList();
    }

    private OrderDto convertToDto(Order order) {
        return new OrderDto(order);
    }

}
