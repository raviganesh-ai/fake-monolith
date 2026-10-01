package com.example.monolith.service;

import com.example.monolith.domain.Customer;
import com.example.monolith.domain.Order;
import com.example.monolith.domain.OrderItem;
import com.example.monolith.domain.OrderStatus;
import com.example.monolith.domain.Product;
import com.example.monolith.repository.CustomerRepository;
import com.example.monolith.repository.OrderRepository;
import com.example.monolith.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order getById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order " + id + " not found"));
    }

    /**
     * Places an order for a customer. The whole operation is transactional:
     * if any line fails validation, nothing is persisted and no stock changes.
     * Duplicate product lines are combined by summing their quantities.
     */
    @Transactional
    public Order placeOrder(Long customerId, List<OrderLineRequest> lines) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new BusinessRuleException("Select a valid customer"));

        Map<Long, Integer> combined = new LinkedHashMap<>();
        if (lines != null) {
            for (OrderLineRequest line : lines) {
                if (line == null || line.getProductId() == null) {
                    continue;
                }
                combined.merge(line.getProductId(), line.getQuantity(), Integer::sum);
            }
        }
        if (combined.isEmpty()) {
            throw new BusinessRuleException("An order must have at least one line item");
        }

        Order order = new Order(customer);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : combined.entrySet()) {
            int quantity = entry.getValue();
            if (quantity <= 0) {
                throw new BusinessRuleException("Quantity must be a positive whole number");
            }
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new BusinessRuleException("Selected product no longer exists"));
            if (quantity > product.getQuantityOnHand()) {
                throw new BusinessRuleException("Insufficient stock for " + product.getName()
                        + " (requested " + quantity + ", available " + product.getQuantityOnHand() + ")");
            }
            BigDecimal lineTotal = product.getUnitPrice().multiply(BigDecimal.valueOf(quantity));
            total = total.add(lineTotal);
            order.addItem(new OrderItem(product, quantity, product.getUnitPrice(), lineTotal));
            product.setQuantityOnHand(product.getQuantityOnHand() - quantity);
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    /**
     * Advances or cancels an order. Legal transitions: NEW -> CONFIRMED -> SHIPPED,
     * and CANCELLED from NEW or CONFIRMED (which restocks the items). A shipped
     * order cannot be cancelled.
     */
    @Transactional
    public Order changeStatus(Long orderId, String action) {
        Order order = getById(orderId);
        String normalized = action == null ? "" : action.trim().toLowerCase();
        switch (normalized) {
            case "confirm":
                if (order.getStatus() != OrderStatus.NEW) {
                    throw new BusinessRuleException("Only a NEW order can be confirmed");
                }
                order.setStatus(OrderStatus.CONFIRMED);
                break;
            case "ship":
                if (order.getStatus() != OrderStatus.CONFIRMED) {
                    throw new BusinessRuleException("Only a CONFIRMED order can be shipped");
                }
                order.setStatus(OrderStatus.SHIPPED);
                break;
            case "cancel":
                if (order.getStatus() == OrderStatus.SHIPPED) {
                    throw new BusinessRuleException("A shipped order cannot be cancelled");
                }
                if (order.getStatus() == OrderStatus.CANCELLED) {
                    throw new BusinessRuleException("Order is already cancelled");
                }
                for (OrderItem item : order.getItems()) {
                    Product product = item.getProduct();
                    product.setQuantityOnHand(product.getQuantityOnHand() + item.getQuantity());
                    productRepository.save(product);
                }
                order.setStatus(OrderStatus.CANCELLED);
                break;
            default:
                throw new BusinessRuleException("Unknown action: " + action);
        }
        return orderRepository.save(order);
    }
}
