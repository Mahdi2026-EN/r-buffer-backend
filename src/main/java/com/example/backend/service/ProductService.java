
package com.example.backend.service;

import com.example.backend.entity.Category;
import com.example.backend.entity.Product;
import com.example.backend.repository.CategoryRepository;
import com.example.backend.repository.ProductRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(
            ProductRepository productRepository,
            CategoryRepository categoryRepository) {

        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Get available products for public browsing.
    @Transactional(readOnly = true)
    public List<Product> getAvailableProducts() {
        return productRepository.findByAvailableTrueOrderByCreatedAtDesc();
    }

    // Get all products for the admin panel.
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc();
    }

    // Get a product by ID.
    @Transactional(readOnly = true)
    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found"
                ));
    }

    // Get available featured products.
    @Transactional(readOnly = true)
    public List<Product> getFeaturedProducts() {
        return productRepository.findByAvailableTrueAndFeaturedTrue();
    }

    // Get available products in a category.
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategory(Long categoryId) {

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Category not found"
            );
        }

        return productRepository.findByCategoryIdAndAvailableTrue(categoryId);
    }

    // Create a product.
    public Product createProduct(Product product) {
        validateProduct(product);

        Category category = getCategory(product.getCategory().getId());

        product.setId(null);
        product.setCategory(category);

        return productRepository.save(product);
    }

    // Update an existing product.
    public Product updateProduct(Long id, Product updatedProduct) {
        validateProduct(updatedProduct);

        Product existingProduct = getProductById(id);

        Category category = getCategory(updatedProduct.getCategory().getId());

        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setCategory(category);
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setImage(updatedProduct.getImage());
        existingProduct.setAvailable(updatedProduct.getAvailable());
        existingProduct.setStock(updatedProduct.getStock());
        existingProduct.setFeatured(updatedProduct.getFeatured());

        return productRepository.save(existingProduct);
    }

    // Delete a product.
    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    // Find the category belonging to the product.
    private Category getCategory(Long categoryId) {

        if (categoryId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product category is required"
            );
        }

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                ));
    }

    // Validate product information.
    private void validateProduct(Product product) {

        if (product == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product information is required"
            );
        }

        if (!hasAllLanguages(product.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name must contain en, fa, and ps translations"
            );
        }

        if (!hasAllLanguages(product.getDescription())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product description must contain en, fa, and ps translations"
            );
        }

        if (product.getPrice() == null
                || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product price cannot be negative"
            );
        }

        if (product.getStock() == null || product.getStock() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product stock cannot be negative"
            );
        }

        if (product.getCategory() == null
                || product.getCategory().getId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product category is required"
            );
        }
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
