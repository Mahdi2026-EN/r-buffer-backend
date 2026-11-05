
package com.example.backend.controller;

import com.example.backend.dto.ProductResponse;
import com.example.backend.entity.Product;
import com.example.backend.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Get available products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {

        List<ProductResponse> products = productService
                .getAvailableProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(products);
    }

    // Admin: Get all products
    @GetMapping("/admin")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {

        List<ProductResponse> products = productService
                .getAllProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(products);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @PathVariable Long id) {

        Product product = productService.getProductById(id);

        return ResponseEntity.ok(ProductResponse.from(product));
    }

    // Get featured products
    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts() {

        List<ProductResponse> products = productService
                .getFeaturedProducts()
                .stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(products);
    }

    // Get products by category
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId) {

        List<ProductResponse> products = productService
                .getProductsByCategory(categoryId)
                .stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(products);
    }

    // Admin: Create product
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody Product product) {

        Product created = productService.createProduct(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ProductResponse.from(created));
    }

    // Admin: Update product
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody Product product) {

        Product updated = productService.updateProduct(id, product);

        return ResponseEntity.ok(ProductResponse.from(updated));
    }

    // Admin: Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        productService.deleteProduct(id);

        return ResponseEntity.noContent().build();
    }
}
