package com.example.backend.service;

import com.example.backend.dto.OrderLineRequest;
import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.*;
import com.example.backend.exception.ApiException;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.ProductRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.util.EnumParser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    // Which status changes an admin may make
    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
            OrderStatus.PENDING, EnumSet.of(OrderStatus.APPROVED, OrderStatus.REJECTED, OrderStatus.CANCELLED),
            OrderStatus.APPROVED, EnumSet.of(OrderStatus.PROCESSING, OrderStatus.CANCELLED),
            OrderStatus.PROCESSING, EnumSet.of(OrderStatus.COMPLETED)
    );

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CreditService creditService;
    private final NotificationService notificationService;

    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        CreditService creditService,
                        NotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.creditService = creditService;
        this.notificationService = notificationService;
    }

    // Customer places an order: credit is spent and stock is reserved right away.
    @Transactional
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User buyer = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        User user = creditService.lockUser(buyer.getId());

        // Merge duplicate lines of the same product
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderLineRequest line : request.items()) {
            quantities.merge(line.productId(), line.quantity(), Integer::sum);
        }

        Order order = new Order();
        order.setUser(user);
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = productRepository.findById(entry.getKey())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"));
            int qty = entry.getValue();

            if (!Boolean.TRUE.equals(product.getAvailable())) {
                throw new ApiException(HttpStatus.CONFLICT, "PRODUCT_UNAVAILABLE", "This product is not available");
            }
            if (product.getStock() < qty) {
                throw new ApiException(HttpStatus.CONFLICT, "OUT_OF_STOCK", "Not enough stock for this product");
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setQuantity(qty);
            item.setUnitPrice(product.getPrice());
            order.addItem(item);

            product.setStock(product.getStock() - qty);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        if (user.getCredit().compareTo(total) < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "INSUFFICIENT_CREDIT",
                    "Insufficient credit. Please add credit to your account.");
        }

        user.setCredit(user.getCredit().subtract(total));
        order.setTotal(total);
        order.setStatus(OrderStatus.PENDING);
        order.addEvent(new OrderEvent(OrderStatus.PENDING));

        Order saved = orderRepository.save(order);
        creditService.ledger(user, CreditTransactionType.USED, total, "Order #" + saved.getId());
        notificationService.notify(user, "order", "Order placed",
                "Your order #" + saved.getId() + " was placed and is waiting for review.");

        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listMine(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getMine(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "User not found"));
        return orderRepository.findByIdAndUserId(id, user.getId())
                .map(OrderResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream().map(OrderResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getAny(Long id) {
        return orderRepository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));
    }

    // Admin changes the status. Rejected or cancelled orders are refunded and their stock returned.
    @Transactional
    public OrderResponse updateStatus(Long id, String statusText, String note) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "ORDER_NOT_FOUND", "Order not found"));

        OrderStatus target = EnumParser.parse(OrderStatus.class, statusText, "INVALID_STATUS");
        Set<OrderStatus> next = ALLOWED.getOrDefault(order.getStatus(), Set.of());

        if (!next.contains(target)) {
            throw new ApiException(HttpStatus.CONFLICT, "INVALID_STATUS_CHANGE",
                    "The order cannot change from " + order.getStatus().name().toLowerCase() + " to " + target.name().toLowerCase());
        }

        if (target == OrderStatus.REJECTED || target == OrderStatus.CANCELLED) {
            refund(order, target);
        }

        order.setStatus(target);
        if (note != null && !note.isBlank()) {
            order.setAdminNote(note.trim());
        }
        order.addEvent(new OrderEvent(target));

        notificationService.notify(order.getUser(), "order", "Order " + target.name().toLowerCase(),
                "Your order #" + order.getId() + " is now " + target.name().toLowerCase() + ".");

        return OrderResponse.from(order);
    }

    private void refund(Order order, OrderStatus reason) {
        User user = creditService.lockUser(order.getUser().getId());
        user.setCredit(user.getCredit().add(order.getTotal()));
        creditService.ledger(user, CreditTransactionType.ADDED, order.getTotal(),
                "Refund for order #" + order.getId() + " (" + reason.name().toLowerCase() + ")");

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
            }
        }
    }
}
