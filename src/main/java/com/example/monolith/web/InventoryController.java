package com.example.monolith.web;

import com.example.monolith.domain.Product;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Web-module controller focused on inventory-related views over products.
 */
@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final ProductService productService;

    public InventoryController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "inventory";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id,
                         @RequestParam int quantityDelta,
                         Model model) {
        try {
            Product product = productService.findById(id);
            int newQuantity = product.getQuantityInStock() + quantityDelta;
            if (newQuantity < 0) {
                throw new BusinessRuleException("Resulting stock cannot be negative");
            }
            product.setQuantityInStock(newQuantity);
            productService.create(product.getName(), product.getPrice(), product.getQuantityInStock());
            return "redirect:/inventory";
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("products", productService.findAll());
            return "inventory";
        }
    }
}
