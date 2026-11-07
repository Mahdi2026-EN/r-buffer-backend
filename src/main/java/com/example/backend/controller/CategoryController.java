
package com.example.backend.controller;

import com.example.backend.dto.CategoryResponse;
import com.example.backend.entity.Category;
import com.example.backend.service.CategoryService;

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

    // Get one active category by ID.
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @PathVariable Long id) {

        Category category =
                categoryService.getActiveCategoryById(id);

        return ResponseEntity.ok(CategoryResponse.from(category));
    }
}
