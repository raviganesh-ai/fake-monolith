package com.example.monolith.web;

import com.example.monolith.domain.Order;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.CustomerService;
import com.example.monolith.service.OrderLineRequest;
import com.example.monolith.service.OrderService;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Web-module controller exposing order-module workflows.
 */
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final ProductService productService;
    private final CustomerService customerService;

    public OrderController(OrderService orderService,
                           ProductService productService,
                           CustomerService customerService) {
        this.orderService = orderService;
        this.productService = productService;
        this.customerService = customerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders";
    }

    @GetMapping("/new")
    public String newOrderForm(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());
        return "new-order";
    }

    @PostMapping
    public String create(@RequestParam Long customerId,
                         @RequestParam("productId") List<Long> productIds,
                         @RequestParam("quantity") List<Integer> quantities,
                         Model model) {
        try {
            List<OrderLineRequest> lines = new ArrayList<>();
            for (int i = 0; i < productIds.size(); i++) {
                lines.add(new OrderLineRequest(productIds.get(i), quantities.get(i)));
            }
            Order order = orderService.createOrder(customerId, lines);
            return "redirect:/orders/" + order.getId();
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("products", productService.findAll());
            return "new-order";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Order order = orderService.findById(id);
        model.addAttribute("order", order);
        return "order-details";
    }
}
