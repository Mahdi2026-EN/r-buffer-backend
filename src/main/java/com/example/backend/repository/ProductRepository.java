
package com.example.backend.repository;

import com.example.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all available products.
    List<Product> findByAvailableTrue();

    // Get available products ordered by newest first.
    List<Product> findByAvailableTrueOrderByCreatedAtDesc();

    // Get available featured products.
    List<Product> findByAvailableTrueAndFeaturedTrue();

    // Get products belonging to a specific category.
    List<Product> findByCategoryId(Long categoryId);

    // Get available products belonging to a specific category.
    List<Product> findByCategoryIdAndAvailableTrue(Long categoryId);

    // Get all products ordered by newest first.
    List<Product> findAllByOrderByCreatedAtDesc();

    // Get featured products.
    List<Product> findByFeaturedTrue();

    // Check whether a category has any products.
    boolean existsByCategoryId(Long categoryId);
}
