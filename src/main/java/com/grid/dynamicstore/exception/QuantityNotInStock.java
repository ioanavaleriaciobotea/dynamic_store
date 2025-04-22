package com.grid.dynamicstore.exception;

public class QuantityNotInStock extends RuntimeException {
    public QuantityNotInStock(String message) {
        super(message);
    }
}
