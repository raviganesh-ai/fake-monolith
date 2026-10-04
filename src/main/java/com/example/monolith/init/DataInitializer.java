package com.example.monolith.init;

import com.example.monolith.domain.Customer;
import com.example.monolith.domain.Product;
import com.example.monolith.repository.CustomerRepository;
import com.example.monolith.repository.ProductRepository;
import com.example.monolith.service.OrderLineRequest;
import com.example.monolith.service.OrderService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Data initialization module used to seed demo data at startup.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderService orderService;

    public DataInitializer(CustomerRepository customerRepository,
                           ProductRepository productRepository,
                           OrderService orderService) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (customerRepository.count() > 0 || productRepository.count() > 0) {
            return;
        }

        Customer alice = customerRepository.save(new Customer("Alice", "alice@example.com"));
        Customer bob = customerRepository.save(new Customer("Bob", "bob@example.com"));

        Product laptop = productRepository.save(new Product("Laptop", 1200.0, 10));
        Product phone = productRepository.save(new Product("Phone", 800.0, 20));

        orderService.createOrder(alice.getId(),
                List.of(new OrderLineRequest(laptop.getId(), 1),
                        new OrderLineRequest(phone.getId(), 2)));

        orderService.createOrder(bob.getId(),
                List.of(new OrderLineRequest(phone.getId(), 1)));
    }
}
