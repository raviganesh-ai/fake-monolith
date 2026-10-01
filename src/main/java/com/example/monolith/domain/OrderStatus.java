package com.example.monolith.domain;

/**
 * Order lifecycle states. Legal transitions are enforced in OrderService:
 * NEW -> CONFIRMED -> SHIPPED, with CANCELLED reachable from NEW or CONFIRMED.
 */
public enum OrderStatus {
    NEW,
    CONFIRMED,
    SHIPPED,
    CANCELLED
}
