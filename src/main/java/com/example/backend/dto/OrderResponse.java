package com.example.backend.dto;

import com.example.backend.entity.Order;
import com.example.backend.entity.OrderEvent;
import com.example.backend.entity.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record OrderResponse(
        String id,
        String userId,
        String userName,
        String userEmail,
        String userPhone,
        List<Item> items,
        BigDecimal total,
        String status,
        String adminNote,
        LocalDateTime createdAt,
        List<Timeline> timeline
) {

    public record Item(String productId, Map<String, String> productName, int quantity, BigDecimal unitPrice) {
    }

    public record Timeline(String status, LocalDateTime date) {
    }

    public static OrderResponse from(Order order) {
        List<Item> items = order.getItems().stream()
                .map((OrderItem i) -> new Item(
                        i.getProduct() != null ? String.valueOf(i.getProduct().getId()) : "",
                        i.getProductName(),
                        i.getQuantity(),
                        i.getUnitPrice()))
                .toList();

        List<Timeline> timeline = order.getEvents().stream()
                .map((OrderEvent e) -> new Timeline(e.getStatus().name().toLowerCase(), e.getCreatedAt()))
                .toList();

        return new OrderResponse(
                String.valueOf(order.getId()),
                String.valueOf(order.getUser().getId()),
                order.getUser().getFullName(),
                order.getUser().getEmail(),
                order.getUser().getPhone(),
                items,
                order.getTotal(),
                order.getStatus().name().toLowerCase(),
                order.getAdminNote(),
                order.getCreatedAt(),
                timeline);
    }
}
