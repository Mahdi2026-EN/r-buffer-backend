package com.example.backend.dto;

import com.example.backend.entity.CmsContent;

import java.util.Map;

public record CmsContentResponse(
        String id,
        String page,
        String section,
        String key,
        Map<String, String> value
) {

    public static CmsContentResponse from(CmsContent c) {
        return new CmsContentResponse(
                String.valueOf(c.getId()),
                c.getPage(),
                c.getSection(),
                c.getContentKey(),
                c.getValue());
    }
}
