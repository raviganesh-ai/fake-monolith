package com.example.monolith.repository;

import com.example.monolith.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the product-module.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
}
