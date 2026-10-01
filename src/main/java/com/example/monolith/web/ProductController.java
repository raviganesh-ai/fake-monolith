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

import java.math.BigDecimal;

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
        return "products/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return "products/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.getById(id);
        model.addAttribute("id", product.getId());
        model.addAttribute("sku", product.getSku());
        model.addAttribute("name", product.getName());
        model.addAttribute("description", product.getDescription());
        model.addAttribute("unitPrice", product.getUnitPrice());
        return "products/form";
    }

    @PostMapping
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam(required = false) String sku,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String description,
                       @RequestParam(required = false) String unitPrice,
                       Model model) {
        try {
            BigDecimal price;
            try {
                price = new BigDecimal(unitPrice == null ? "" : unitPrice.trim());
            } catch (NumberFormatException ex) {
                throw new BusinessRuleException("Price must be a number");
            }
            productService.save(id, sku, name, description, price);
            return "redirect:/products";
        } catch (BusinessRuleException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("id", id);
            model.addAttribute("sku", sku);
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("unitPrice", unitPrice);
            return "products/form";
        }
    }
}
