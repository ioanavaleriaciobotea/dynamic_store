package com.grid.dynamicstore.exception;

public class WrongUserRoleException extends RuntimeException {
    public WrongUserRoleException(String message) {
        super(message);
    }
}
