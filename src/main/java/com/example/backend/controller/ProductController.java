package com.example.backend.controller;

import com.example.backend.dto.ProductRequest;
import com.example.backend.dto.ProductResponse;
import com.example.backend.service.ProductService;
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

    // Public: available products
    @GetMapping
    public List<ProductResponse> getAvailableProducts() {
        return productService.getAvailableProducts().stream().map(ProductResponse::from).toList();
    }

    // Public: featured products
    @GetMapping("/featured")
    public List<ProductResponse> getFeaturedProducts() {
        return productService.getFeaturedProducts().stream().map(ProductResponse::from).toList();
    }

    // Public: available products in one category
    @GetMapping("/category/{categoryId}")
    public List<ProductResponse> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategory(categoryId).stream().map(ProductResponse::from).toList();
    }

    // Admin: every product, including unavailable ones
    @GetMapping("/admin")
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts().stream().map(ProductResponse::from).toList();
    }

    // Public: one product
    @GetMapping("/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return ProductResponse.from(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductResponse.from(productService.createProduct(request)));
    }

    @PutMapping("/{id}")
    public ProductResponse updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ProductResponse.from(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
