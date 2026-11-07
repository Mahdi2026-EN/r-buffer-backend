package com.example.backend.controller;

import com.example.backend.dto.AdminStatsResponse;
import com.example.backend.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    private final StatsService statsService;

    public AdminStatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping
    public AdminStatsResponse stats() {
        return statsService.stats();
    }
}
