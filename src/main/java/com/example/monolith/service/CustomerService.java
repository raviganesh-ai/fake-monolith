package com.example.monolith.service;

import com.example.monolith.domain.Customer;
import com.example.monolith.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer " + id + " not found"));
    }

    @Transactional
    public Customer create(String name, String email, String phone, String address) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessRuleException("Customer name is required");
        }
        return customerRepository.save(new Customer(name.trim(), email, phone, address));
    }
}
