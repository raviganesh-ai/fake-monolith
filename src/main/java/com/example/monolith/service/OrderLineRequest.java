package com.example.monolith.service;

/**
 * A single requested order line (product plus quantity), used as input to
 * OrderService.placeOrder.
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
