
package com.example.backend.service;

import com.example.backend.dto.CategoryRequest;
import com.example.backend.entity.Category;
import com.example.backend.repository.CategoryRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Get all active categories for public browsing.
    @Transactional(readOnly = true)
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
    }

    // Get all categories for the admin panel.
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    // Get a category by ID, including inactive categories.
    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Category not found"
                ));
    }

    // Get a category only if it is active.
    @Transactional(readOnly = true)
    public Category getActiveCategoryById(Long id) {
        return categoryRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Active category not found"
                ));
    }

    // Create a new category.
    public Category createCategory(CategoryRequest request) {
        validateCategory(request);

        Category category = new Category();

        category.setName(request.name());
        category.setDescription(request.description());
        category.setIcon(request.icon());
        category.setImage(request.image());

        category.setDisplayOrder(
                request.order() != null ? request.order() : 0
        );

        category.setActive(
                request.active() != null ? request.active() : true
        );

        return categoryRepository.save(category);
    }

    // Update an existing category.
    public Category updateCategory(Long id, CategoryRequest request) {
        validateCategory(request);

        Category existingCategory = getCategoryById(id);

        existingCategory.setName(request.name());
        existingCategory.setDescription(request.description());
        existingCategory.setIcon(request.icon());
        existingCategory.setImage(request.image());

        if (request.order() != null) {
            existingCategory.setDisplayOrder(request.order());
        }

        if (request.active() != null) {
            existingCategory.setActive(request.active());
        }

        return categoryRepository.save(existingCategory);
    }

    // Safely deactivate a category instead of permanently deleting it.
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);

        category.setActive(false);

        categoryRepository.save(category);
    }

    // Validate incoming category information.
    private void validateCategory(CategoryRequest request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category information is required"
            );
        }

        if (!hasAllLanguages(request.name())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category name must contain en, fa, and ps translations"
            );
        }

        if (request.description() != null
                && !hasAllLanguages(request.description())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category description must contain en, fa, and ps translations"
            );
        }

        if (request.order() != null && request.order() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Display order cannot be negative"
            );
        }
    }

    // Check that all three language translations exist.
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
