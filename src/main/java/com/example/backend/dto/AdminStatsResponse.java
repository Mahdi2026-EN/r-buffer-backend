package com.example.backend.dto;

import java.math.BigDecimal;
import java.util.List;

public record AdminStatsResponse(
        long totalUsers,
        long pendingUsers,
        long approvedUsers,
        BigDecimal totalCredit,
        long pendingPayments,
        long totalOrders,
        long pendingOrders,
        long completedOrders,
        long productsCount,
        List<DailyPoint> daily,
        List<NamedValue> productActivity
) {

    // One point per day for the last 14 days (date is YYYY-MM-DD)
    public record DailyPoint(String date, long orders, BigDecimal added, BigDecimal used, long registered) {
    }

    public record NamedValue(String name, long value) {
    }
}
