
package com.example.backend.dto;

import com.example.backend.entity.Category;

import java.util.Map;

public record CategoryResponse(
        String id,
        Map<String, String> name,
        Map<String, String> description,
        String icon,
        String image,
        Integer order,
        Boolean active
) {

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                String.valueOf(category.getId()),
                category.getName(),
                category.getDescription(),
                category.getIcon(),
                category.getImage(),
                category.getDisplayOrder(),
                category.getActive()
        );
    }
}
