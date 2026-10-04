package com.example.monolith.repository;

import com.example.monolith.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the order-module.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
