package com.example.monolith.service;

import com.example.monolith.domain.Product;
import com.example.monolith.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
    }

    /**
     * Creates a new product (id null) or updates an existing one. Enforces a
     * non-negative price and a unique SKU.
     */
    @Transactional
    public Product save(Long id, String sku, String name, String description, BigDecimal unitPrice) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new BusinessRuleException("SKU is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessRuleException("Product name is required");
        }
        if (unitPrice == null || unitPrice.signum() < 0) {
            throw new BusinessRuleException("Price must be zero or greater");
        }
        Optional<Product> existingBySku = productRepository.findBySku(sku.trim());
        if (existingBySku.isPresent() && !existingBySku.get().getId().equals(id)) {
            throw new BusinessRuleException("A product with SKU " + sku.trim() + " already exists");
        }
        Product product;
        if (id != null) {
            product = getById(id);
            product.setSku(sku.trim());
            product.setName(name.trim());
            product.setDescription(description);
            product.setUnitPrice(unitPrice);
        } else {
            product = new Product(sku.trim(), name.trim(), description, unitPrice, 0);
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product adjustStock(Long id, int newQuantity) {
        if (newQuantity < 0) {
            throw new BusinessRuleException("Stock quantity must be zero or greater");
        }
        Product product = getById(id);
        product.setQuantityOnHand(newQuantity);
        return productRepository.save(product);
    }
}
