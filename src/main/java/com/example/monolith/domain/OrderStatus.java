package com.example.monolith.domain;

/**
 * Order lifecycle states for the order-module.
 */
public enum OrderStatus {
    NEW,
    PAID,
    SHIPPED,
    CANCELLED
}
