package com.example.monolith.service;

import com.example.monolith.domain.Customer;
import com.example.monolith.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Customer-module service encapsulating customer business logic.
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
    }

    public Customer create(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Customer name is required");
        }
        Customer customer = new Customer(name, email);
        return customerRepository.save(customer);
    }
}
