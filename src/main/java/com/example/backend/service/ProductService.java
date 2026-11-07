package com.example.backend.service;

import com.example.backend.dto.ProductRequest;
import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailableTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Product not found"));
    }

    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findByAvailableTrueAndFeaturedTrue();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found");
        }
        return productRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    public Product createProduct(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductRequest request) {
        Product product = getProductById(id);
        apply(product, request);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.delete(getProductById(id));
    }

    private void apply(Product product, ProductRequest request) {
        if (!hasAllLanguages(request.name())) {
            throw bad("Product name must contain en, fa, and ps translations");
        }
        if (!hasAllLanguages(request.description())) {
            throw bad("Product description must contain en, fa, and ps translations");
        }
        if (request.price() == null || request.price().compareTo(BigDecimal.ZERO) < 0) {
            throw bad("Product price cannot be negative");
        }
        if (request.stock() == null || request.stock() < 0) {
            throw bad("Product stock cannot be negative");
        }
        if (request.categoryId() == null) {
            throw bad("Product category is required");
        }

        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "NOT_FOUND", "Category not found"));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setCategory(category);
        product.setPrice(request.price());
        product.setImage(request.image());
        product.setAvailable(request.available() == null || request.available());
        product.setStock(request.stock());
        product.setFeatured(Boolean.TRUE.equals(request.featured()));
    }

    private static ApiException bad(String message) {
        return new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    private boolean hasAllLanguages(Map<String, String> translations) {
        return translations != null
                && hasText(translations.get("en"))
                && hasText(translations.get("fa"))
                && hasText(translations.get("ps"));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
