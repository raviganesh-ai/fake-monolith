package com.example.monolith.service;

/**
 * Thrown when a business rule is violated (for example insufficient stock, a
 * duplicate SKU, or an illegal order-status transition). The web layer turns
 * this into a user-facing validation message.
 */
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
