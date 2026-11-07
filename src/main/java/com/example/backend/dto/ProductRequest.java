package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.Map;

public record ProductRequest(
        Map<String, String> name,
        Map<String, String> description,
        Long categoryId,
        BigDecimal price,
        String image,
        Boolean available,
        Integer stock,
        Boolean featured
) {
}
