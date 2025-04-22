package com.grid.dynamicstore.exception;

public class ProductNotInCart extends RuntimeException {
    public ProductNotInCart(String message) {
        super(message);
    }
}
