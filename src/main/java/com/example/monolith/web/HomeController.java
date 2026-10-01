package com.example.monolith.web;

import com.example.monolith.service.CustomerService;
import com.example.monolith.service.OrderService;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final CustomerService customerService;
    private final ProductService productService;
    private final OrderService orderService;

    public HomeController(CustomerService customerService, ProductService productService, OrderService orderService) {
        this.customerService = customerService;
        this.productService = productService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("customerCount", customerService.findAll().size());
        model.addAttribute("productCount", productService.findAll().size());
        model.addAttribute("orderCount", orderService.findAll().size());
        return "index";
    }
}
