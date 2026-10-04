package com.example.monolith.web;

import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.CustomerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Web-module controller exposing customer-module functionality.
 */
@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.findAll());
        return "customers";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam(required = false) String email,
                         Model model) {
        try {
            customerService.create(name, email);
            return "redirect:/customers";
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("customers", customerService.findAll());
            return "customers";
        }
    }
}
