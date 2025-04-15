package com.grid.dynamicstore.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
public class CartDto {
    private final Map<Long, Integer> productQuantities = new HashMap<>();

    // Add product (or increase quantity)
    public void addProduct(Long productId, int quantity) {
        productQuantities.merge(productId, quantity, Integer::sum);
    }

    // Set quantity explicitly
    public void updateProduct(Long productId, int quantity) {
        if (quantity <= 0) {
            productQuantities.remove(productId);
        } else {
            productQuantities.put(productId, quantity);
        }
    }

    // Remove product from cart
    public void removeProduct(Long productId) {
        productQuantities.remove(productId);
    }

    // Clear entire cart
    public void clear() {
        productQuantities.clear();
    }

    public Map<Long, Integer> getProductQuantities() {
        return productQuantities;
    }

    public boolean isEmpty() {
        return productQuantities.isEmpty();
    }
}
