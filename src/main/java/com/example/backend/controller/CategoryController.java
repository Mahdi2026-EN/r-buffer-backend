
package com.example.backend.controller;

import com.example.backend.dto.CategoryRequest;
import com.example.backend.dto.CategoryResponse;
import com.example.backend.entity.Category;
import com.example.backend.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all active categories for public browsing.
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {

        List<CategoryResponse> categories =
                categoryService.getActiveCategories()
                        .stream()
                        .map(CategoryResponse::from)
                        .toList();

        return ResponseEntity.ok(categories);
    }

    // Get all categories for the admin panel.
    @GetMapping("/admin")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories()
                        .stream()
                        .map(CategoryResponse::from)
                        .toList();

        return ResponseEntity.ok(categories);
    }

    // Get an active category by ID for public browsing.
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        Category category = categoryService.getActiveCategoryById(id);

        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    // Create a new category.
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest request) {

        Category category = categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CategoryResponse.from(category));
    }

    // Update an existing category.
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {

        Category category = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    // Delete a category only if it has no products.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
