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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private OrderServiceImpl orderService;

    private final String sessionId = "session123";
    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        product = new Product();
        product.setId(1L);
        product.setAvailable(10);
        product.setPrice(BigDecimal.valueOf(100));
        product.setTitle("Test Product");

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn(user.getEmail());

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
    }

    @Test
    void testCheckoutSuccess() {
        CartDto cart = new CartDto();
        cart.addProduct(product.getId(), 2);

        when(cartService.getCart(sessionId)).thenReturn(cart);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDto result = orderService.checkout(sessionId);

        assertNotNull(result);
        assertEquals(OrderStatus.CREATED, result.getStatus());
        verify(cartService).clearCart(sessionId);
    }

    @Test
    void testCheckoutWithEmptyCart() {
        when(cartService.getCart(sessionId)).thenReturn(new CartDto());

        assertThrows(EmptyCart.class, () -> orderService.checkout(sessionId));
    }

    @Test
    void testCheckoutWithUnavailableProduct() {
        CartDto cart = new CartDto();
        cart.addProduct(product.getId(), 11); // Exceeds available

        when(cartService.getCart(sessionId)).thenReturn(cart);
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        assertThrows(QuantityNotInStock.class, () -> orderService.checkout(sessionId));
    }

    @Test
    void testCheckoutWithMissingProduct() {
        CartDto cart = new CartDto();
        cart.addProduct(999L, 1);

        when(cartService.getCart(sessionId)).thenReturn(cart);
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.checkout(sessionId));
    }

    @Test
    void testCancelOrderSuccess() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);

        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setProduct(product);
        order.setOrderItems(Set.of(item));

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.cancelOrder(1L, sessionId);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(12, product.getAvailable());
        verify(orderRepository).save(order);
    }

    @Test
    void testCancelOrderAlreadyCancelled() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class, () -> orderService.cancelOrder(1L, sessionId));
    }

    @Test
    void testCancelOrderDifferentUser() {
        User anotherUser = new User();
        anotherUser.setId(99L);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setUser(anotherUser);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(SecurityException.class, () -> orderService.cancelOrder(1L, sessionId));
    }

    @Test
    void testGetOrdersReturnsList() {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotal(BigDecimal.TEN);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());

        when(orderRepository.findByUser(user)).thenReturn(List.of(order));

        List<OrderDto> result = orderService.getOrders(sessionId);

        assertEquals(1, result.size());
        assertEquals(order.getId(), result.get(0).getId());
    }
}
