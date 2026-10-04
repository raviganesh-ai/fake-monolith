package com.example.monolith.service;

/**
 * Domain-specific not-found exception mapped to 404 responses.
 */
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
