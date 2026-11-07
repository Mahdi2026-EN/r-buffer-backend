package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.OrderStatusRequest;
import com.example.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // Customer: place an order
    @PostMapping("/api/orders")
    public ResponseEntity<OrderResponse> placeOrder(Authentication auth,
                                                    @Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(auth.getName(), request));
    }

    // Customer: my orders
    @GetMapping("/api/orders/mine")
    public List<OrderResponse> myOrders(Authentication auth) {
        return orderService.listMine(auth.getName());
    }

    // Customer: one of my orders
    @GetMapping("/api/orders/mine/{id}")
    public OrderResponse myOrder(Authentication auth, @PathVariable Long id) {
        return orderService.getMine(auth.getName(), id);
    }

    // Admin: all orders
    @GetMapping("/api/admin/orders")
    public List<OrderResponse> allOrders() {
        return orderService.listAll();
    }

    // Admin: one order
    @GetMapping("/api/admin/orders/{id}")
    public OrderResponse anyOrder(@PathVariable Long id) {
        return orderService.getAny(id);
    }

    // Admin: approve / process / complete / reject / cancel
    @PatchMapping("/api/admin/orders/{id}/status")
    public OrderResponse updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return orderService.updateStatus(id, request.status(), request.note());
    }
}
