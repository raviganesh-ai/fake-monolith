package com.example.monolith.service;

/**
 * Domain-specific business rule violation used across modules.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
