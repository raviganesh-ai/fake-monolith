package com.example.monolith.web;

import com.example.monolith.domain.Product;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Web-module controller exposing product-module operations.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "products";
    }

    @PostMapping
    public String create(@RequestParam String name,
                         @RequestParam double price,
                         @RequestParam int quantityInStock,
                         Model model) {
        try {
            productService.create(name, price, quantityInStock);
            return "redirect:/products";
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("products", productService.findAll());
            return "products";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "product-details";
    }
}
