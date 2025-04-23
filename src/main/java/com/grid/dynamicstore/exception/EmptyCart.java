package com.grid.dynamicstore.exception;

public class EmptyCart extends RuntimeException {
    public EmptyCart(String message) {
        super(message);
    }
}
