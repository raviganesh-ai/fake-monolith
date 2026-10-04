package com.example.monolith.service;

/**
 * Input model used by the web-module to describe a requested order line.
 */
public class OrderLineRequest {

    private Long productId;

    private int quantity;

    public OrderLineRequest() {
    }

    public OrderLineRequest(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
