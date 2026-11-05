
package com.example.backend.dto;

import com.example.backend.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

public record ProductResponse(
        String id,
        Map<String, String> name,
        Map<String, String> description,
        String categoryId,
        BigDecimal price,
        String image,
        Boolean available,
        Integer stock,
        LocalDateTime createdAt,
        Boolean featured
) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                String.valueOf(product.getId()),
                product.getName(),
                product.getDescription(),
                String.valueOf(product.getCategory().getId()),
                product.getPrice(),
                product.getImage(),
                product.getAvailable(),
                product.getStock(),
                product.getCreatedAt(),
                product.getFeatured()
        );
    }
}
