package com.example.monolith.web;

import com.example.monolith.domain.Product;
import com.example.monolith.service.BusinessRuleException;
import com.example.monolith.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "inventory/list";
    }

    @GetMapping("/{id}/adjust")
    public String adjustForm(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("product", product);
        model.addAttribute("quantityOnHand", product.getQuantityOnHand());
        return "inventory/adjust";
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable Long id,
                         @RequestParam(required = false) String quantityOnHand,
                         Model model) {
        try {
            int quantity;
            try {
                quantity = Integer.parseInt(quantityOnHand == null ? "" : quantityOnHand.trim());
            } catch (NumberFormatException ex) {
                throw new BusinessRuleException("Quantity must be a whole number");
            }
            productService.adjustStock(id, quantity);
            return "redirect:/inventory";
        } catch (BusinessRuleException ex) {
            model.addAttribute("product", productService.getById(id));
            model.addAttribute("quantityOnHand", quantityOnHand);
            model.addAttribute("error", ex.getMessage());
            return "inventory/adjust";
        }
    }
}
