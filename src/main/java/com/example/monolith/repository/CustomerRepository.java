package com.example.monolith.repository;

import com.example.monolith.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for the customer-module.
 */
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
