package com.example.monolith.service;

import com.example.monolith.domain.*;
import com.example.monolith.repository.CustomerRepository;
import com.example.monolith.repository.OrderRepository;
import com.example.monolith.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Order-module service orchestrating orders across customers and products.
 */
@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found: " + id));
    }

    public Order createOrder(Long customerId, List<OrderLineRequest> lineRequests) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + customerId));

        if (lineRequests == null || lineRequests.isEmpty()) {
            throw new BusinessRuleException("Order must contain at least one line");
        }

        Order order = new Order(customer.getId(), customer.getName());

        for (OrderLineRequest lineRequest : lineRequests) {
            Product product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + lineRequest.getProductId()));

            if (!product.hasSufficientStock(lineRequest.getQuantity())) {
                throw new BusinessRuleException("Insufficient stock for product: " + product.getId());
            }

            product.decreaseStock(lineRequest.getQuantity());

            OrderItem item = new OrderItem(
                    product.getId(),
                    product.getName(),
                    lineRequest.getQuantity(),
                    product.getPrice());
            order.addItem(item);
        }

        order.setStatus(OrderStatus.NEW);
        return orderRepository.save(order);
    }
}
