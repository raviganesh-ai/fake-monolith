package com.example.monolith;

import com.example.monolith.domain.Customer;
import com.example.monolith.domain.Order;
import com.example.monolith.domain.OrderStatus;
import com.example.monolith.domain.Product;
import com.example.monolith.repository.OrderRepository;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.CustomerService;
import com.example.monolith.service.OrderLineRequest;
import com.example.monolith.service.OrderService;
import com.example.monolith.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Minimal acceptance tests for release R-Z7UL7V (AC-1 through AC-4).
 * Loading the Spring context also exercises that the application starts.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OrderManagementAcceptanceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    /** AC-1: the application starts and the home page returns HTTP 200. */
    @Test
    void ac1_homePageReturns200() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk());
    }

    /** AC-2: placing a valid order decrements stock and computes line and order totals. */
    @Test
    void ac2_validOrderDecrementsStockAndComputesTotals() {
        Customer customer = customerService.create("AC2 Customer", null, null, null);
        Product product = productService.save(null, "AC2-SKU", "AC2 Product", null, new BigDecimal("10.00"));
        productService.adjustStock(product.getId(), 5);

        Order order = orderService.placeOrder(customer.getId(),
                Collections.singletonList(new OrderLineRequest(product.getId(), 3)));

        assertEquals(OrderStatus.NEW, order.getStatus());
        assertEquals(0, new BigDecimal("30.00").compareTo(order.getTotalAmount()));
        assertEquals(1, order.getItems().size());
        assertEquals(0, new BigDecimal("30.00").compareTo(order.getItems().get(0).getLineTotal()));
        assertEquals(2, productService.getById(product.getId()).getQuantityOnHand());
    }

    /** AC-3: an over-stock order is rejected transactionally (no stock change, no order persisted). */
    @Test
    void ac3_overStockOrderRejectedTransactionally() {
        Customer customer = customerService.create("AC3 Customer", null, null, null);
        Product a = productService.save(null, "AC3-A", "AC3 Product A", null, new BigDecimal("10.00"));
        productService.adjustStock(a.getId(), 5);
        Product b = productService.save(null, "AC3-B", "AC3 Product B", null, new BigDecimal("10.00"));
        productService.adjustStock(b.getId(), 2);
        long ordersBefore = orderRepository.count();

        assertThrows(BusinessRuleException.class, () ->
                orderService.placeOrder(customer.getId(), Arrays.asList(
                        new OrderLineRequest(a.getId(), 2),
                        new OrderLineRequest(b.getId(), 5))));

        // Line A would have decremented first; the rollback must restore it.
        assertEquals(5, productService.getById(a.getId()).getQuantityOnHand());
        assertEquals(2, productService.getById(b.getId()).getQuantityOnHand());
        assertEquals(ordersBefore, orderRepository.count());
    }

    /** AC-4: cancelling an order restocks its items. */
    @Test
    void ac4_cancellingOrderRestocksItems() {
        Customer customer = customerService.create("AC4 Customer", null, null, null);
        Product product = productService.save(null, "AC4-SKU", "AC4 Product", null, new BigDecimal("10.00"));
        productService.adjustStock(product.getId(), 5);

        Order order = orderService.placeOrder(customer.getId(),
                Collections.singletonList(new OrderLineRequest(product.getId(), 3)));
        assertEquals(2, productService.getById(product.getId()).getQuantityOnHand());

        orderService.changeStatus(order.getId(), "cancel");

        assertEquals(5, productService.getById(product.getId()).getQuantityOnHand());
        assertEquals(OrderStatus.CANCELLED, orderService.getById(order.getId()).getStatus());
    }
}
