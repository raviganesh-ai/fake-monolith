package com.example.monolith.service;

import com.example.monolith.domain.Product;
import com.example.monolith.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Product & inventory-module service encapsulating catalog and stock rules.
 */
@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
    }

    public Product create(String name, double price, int quantityInStock) {
        if (name == null || name.isBlank()) {
            throw new BusinessRuleException("Product name is required");
        }
        if (price < 0) {
            throw new BusinessRuleException("Product price cannot be negative");
        }
        if (quantityInStock < 0) {
            throw new BusinessRuleException("Initial stock cannot be negative");
        }
        Product product = new Product(name, price, quantityInStock);
        return productRepository.save(product);
    }

    public void decreaseStock(Product product, int quantity) {
        if (!product.hasSufficientStock(quantity)) {
            throw new BusinessRuleException("Insufficient stock for product: " + product.getId());
        }
        product.decreaseStock(quantity);
        productRepository.save(product);
    }
}
