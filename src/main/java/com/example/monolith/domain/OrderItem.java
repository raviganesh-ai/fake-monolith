package com.example.monolith.domain;

import jakarta.persistence.Embeddable;

/**
 * Line item within an Order aggregate.
 */
@Embeddable
public class OrderItem {

    private Long productId;

    private String productName;

    private int quantity;

    private double unitPrice;

    protected OrderItem() {
        // JPA
    }

    public OrderItem(Long productId, String productName, int quantity, double unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }
}
