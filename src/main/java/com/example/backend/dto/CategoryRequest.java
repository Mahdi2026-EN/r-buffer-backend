
package com.example.backend.dto;

import java.util.Map;

public record CategoryRequest(

        Map<String, String> name,

        Map<String, String> description,

        String icon,

        String image,

        Integer order,

        Boolean active

) {
}
