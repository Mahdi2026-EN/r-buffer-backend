
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
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // Get all categories, including inactive ones.
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {

        List<CategoryResponse> categories =
                categoryService.getAllCategories()
                        .stream()
                        .map(CategoryResponse::from)
                        .toList();

        return ResponseEntity.ok(categories);
    }

    // Get one category by ID.
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        Category category = categoryService.getCategoryById(id);

        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    // Create a category.
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @RequestBody CategoryRequest request) {

        Category category = categoryService.createCategory(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CategoryResponse.from(category));
    }

    // Update a category.
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request) {

        Category category =
                categoryService.updateCategory(id, request);

        return ResponseEntity.ok(CategoryResponse.from(category));
    }

    // Deactivate a category instead of permanently deleting it.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id) {

        categoryService.deleteCategory(id);

        return ResponseEntity.noContent().build();
    }
}
