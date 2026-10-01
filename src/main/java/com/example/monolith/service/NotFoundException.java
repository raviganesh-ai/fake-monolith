package com.example.monolith.service;

/**
 * Thrown when a requested entity does not exist. Mapped to an HTTP 404 page by
 * the web layer's exception handler.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
