
package com.example.backend.repository;

import com.example.backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Get active categories ordered by display order.
    List<Category> findByActiveTrueOrderByDisplayOrderAsc();

    // Get all categories, including inactive ones.
    List<Category> findAllByOrderByDisplayOrderAsc();

    // Find a category by ID only if it is active.
    Optional<Category> findByIdAndActiveTrue(Long id);
}
