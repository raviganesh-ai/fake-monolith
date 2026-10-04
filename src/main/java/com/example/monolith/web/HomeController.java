package com.example.monolith.web;

import com.example.monolith.service.CustomerService;
import com.example.monolith.service.OrderService;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web-module controller for the home page dashboard.
 */
@Controller
public class HomeController {

    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public HomeController(ProductService productService,
                          CustomerService customerService,
                          OrderService orderService) {
        this.productService = productService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("customers", customerService.findAll());
        model.addAttribute("orders", orderService.findAll());
        return "home";
    }
}
