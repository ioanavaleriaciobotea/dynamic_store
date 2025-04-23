package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.dto.OrderDto;
import com.grid.dynamicstore.exception.EmptyCart;
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
            throw new EmptyCart("Cart is empty.");
        }

        User user = getCurrentUser();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        Set<OrderItem> items = findOrderItems(cart.getProductQuantities(), order);
        order.setOrderItems(items);

        BigDecimal total = calculateTotal(items);
        order.setTotal(total);

        orderRepository.save(order);

        cartService.clearCart(sessionId);

        return new OrderDto(order);
    }

    private BigDecimal calculateTotal(Set<OrderItem> items) {
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : items) {
            total = total.add(item.getTotalPrice());
        }

        return total;
    }
    
    private Set<OrderItem> findOrderItems(Map<Long, Integer> productQuantities, Order order) {
        Set<OrderItem> items = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long productId = entry.getKey();
            int quantity = entry.getValue();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product not found."));

            OrderItem item = getOrderItem(order, product, quantity);

            items.add(item);
            
            product.setAvailable(product.getAvailable() - quantity);
        }
        
        return items;
    }

    private static OrderItem getOrderItem(Order order, Product product, int quantity) {
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
        return item;
    }

    @Override
    public List<OrderDto> getOrders(String sessionId) {
        User user = getCurrentUser();

        return orderRepository.findByUser(user).stream()
                .map(OrderDto::new)
                .toList();
    }

    @Override
    public void cancelOrder(Long orderId, String sessionId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found."));

        User user = getCurrentUser();

        if (!order.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You cannot cancel someone else's order.");
        }

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled.");
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setAvailable(product.getAvailable() + item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);

        orderRepository.save(order);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));
    }
}
