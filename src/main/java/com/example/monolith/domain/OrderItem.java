package com.example.monolith.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * A single line within an order. Captures the unit price at order time so that
 * later product-price changes do not alter historical orders.
 */
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private BigDecimal unitPriceAtOrder;

    @Column(nullable = false)
    private BigDecimal lineTotal;

    public OrderItem() {
    }

    public OrderItem(Product product, int quantity, BigDecimal unitPriceAtOrder, BigDecimal lineTotal) {
        this.product = product;
        this.quantity = quantity;
        this.unitPriceAtOrder = unitPriceAtOrder;
        this.lineTotal = lineTotal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPriceAtOrder() {
        return unitPriceAtOrder;
    }

    public void setUnitPriceAtOrder(BigDecimal unitPriceAtOrder) {
        this.unitPriceAtOrder = unitPriceAtOrder;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}
