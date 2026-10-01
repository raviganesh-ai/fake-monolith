package com.example.monolith.web;

import com.example.monolith.domain.Order;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.CustomerService;
import com.example.monolith.service.OrderLineRequest;
import com.example.monolith.service.OrderService;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;

    public OrderController(OrderService orderService, CustomerService customerService, ProductService productService) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("products", productService.findAll());
        model.addAttribute("rows", new int[]{0, 1, 2, 3, 4});
        return "orders/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Order order = orderService.getById(id);
        model.addAttribute("order", order);
        return "orders/detail";
    }

    @PostMapping
    public String place(@RequestParam(required = false) Long customerId,
                        @RequestParam(name = "productId", required = false) List<String> productIds,
                        @RequestParam(name = "quantity", required = false) List<String> quantities,
                        Model model) {
        try {
            List<OrderLineRequest> lines = new ArrayList<>();
            if (productIds != null) {
                for (int i = 0; i < productIds.size(); i++) {
                    String rawProductId = productIds.get(i);
                    if (rawProductId == null || rawProductId.trim().isEmpty()) {
                        continue;
                    }
                    Long productId = Long.valueOf(rawProductId.trim());
                    String rawQty = (quantities != null && i < quantities.size()) ? quantities.get(i) : "";
                    int quantity;
                    try {
                        quantity = (rawQty == null || rawQty.trim().isEmpty()) ? 0 : Integer.parseInt(rawQty.trim());
                    } catch (NumberFormatException ex) {
                        throw new BusinessRuleException("Quantity must be a whole number");
                    }
                    lines.add(new OrderLineRequest(productId, quantity));
                }
            }
            if (customerId == null) {
                throw new BusinessRuleException("Select a customer");
            }
            Order order = orderService.placeOrder(customerId, lines);
            return "redirect:/orders/" + order.getId();
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            model.addAttribute("products", productService.findAll());
            model.addAttribute("rows", new int[]{0, 1, 2, 3, 4});
            return "orders/form";
        }
    }

    @PostMapping("/{id}/status")
    public String changeStatus(@PathVariable Long id,
                               @RequestParam String action,
                               Model model) {
        try {
            orderService.changeStatus(id, action);
            return "redirect:/orders/" + id;
        } catch (BusinessRuleException ex) {
            model.addAttribute("order", orderService.getById(id));
            model.addAttribute("error", ex.getMessage());
            return "orders/detail";
        }
    }
}
