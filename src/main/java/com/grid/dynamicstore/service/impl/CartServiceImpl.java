package com.grid.dynamicstore.service.impl;

import com.grid.dynamicstore.dto.CartDto;
import com.grid.dynamicstore.dto.CartItemDto;
import com.grid.dynamicstore.dto.CartResponseDto;
import com.grid.dynamicstore.exception.EntityNotFoundException;
import com.grid.dynamicstore.exception.QuantityNotInStock;
import com.grid.dynamicstore.model.Product;
import com.grid.dynamicstore.repository.ProductRepository;
import com.grid.dynamicstore.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    private final Map<String, CartDto> carts = new ConcurrentHashMap<>();

    public CartServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void addProduct(String sessionId, Long productId, int quantity) {

        validateProductAvailability(productId, quantity);

        getCart(sessionId).addProduct(productId, quantity);
    }

    @Override
    public CartResponseDto viewCart(String sessionId) {
        CartDto cart = getCart(sessionId);

        if (cart.isEmpty()) {
            return new CartResponseDto(List.of());
        }

        List<CartItemDto> items = cart.getProductQuantities().entrySet().stream()
                .map(entry -> {
                    Long productId = entry.getKey();
                    int quantity = entry.getValue();
                    return productRepository.findById(productId)
                            .map(product -> new CartItemDto(product, quantity))
                            .orElse(null);
                })
                .filter(item -> item != null)
                .collect(Collectors.toList());

        for (int i = 0; i < items.size(); i++) {
            items.get(i).setOrdinal(i + 1);
        }

        return new CartResponseDto(items);
    }

    @Override
    public void removeProduct(String sessionId, Long productId) {
        CartDto cart = getCart(sessionId);

        validateProductInCart(sessionId, productId);

        cart.removeProduct(productId);
    }

    @Override
    public CartItemDto updateProduct(String sessionId, Long productId, int quantity) {
        CartDto cart = getCart(sessionId);

        validateProductInCart(sessionId, productId);

        Product product = validateProductAvailability(productId, quantity);

        cart.updateProduct(productId, quantity);
        return new CartItemDto(product, quantity);
    }

    @Override
    public CartDto getCart(String sessionId) {
        return carts.computeIfAbsent(sessionId, id -> new CartDto());
    }

    @Override
    public void clearCart(String sessionId) {
        carts.remove(sessionId);
    }

    private Product validateProductAvailability(Long productId, int quantity) {
        Product product = findProductById(productId);
        if (product.getAvailable() < quantity) {
            throw new QuantityNotInStock("Not enough quantity in stock.");
        }
        return product;
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private void validateProductInCart(String sessionId, Long productId) {
        CartDto cart = getCart(sessionId);
        if (!cart.getProductQuantities().containsKey(productId)) {
            throw new RuntimeException("Product not found in cart!");
        }
    }

}
