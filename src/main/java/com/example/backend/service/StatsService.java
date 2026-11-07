package com.example.backend.service;

import com.example.backend.dto.AdminStatsResponse;
import com.example.backend.dto.AdminStatsResponse.DailyPoint;
import com.example.backend.dto.AdminStatsResponse.NamedValue;
import com.example.backend.entity.*;
import com.example.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private static final int DAYS = 14;

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PaymentRequestRepository paymentRequestRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final ProductRepository productRepository;

    public StatsService(UserRepository userRepository,
                        OrderRepository orderRepository,
                        PaymentRequestRepository paymentRequestRepository,
                        CreditTransactionRepository creditTransactionRepository,
                        ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.paymentRequestRepository = paymentRequestRepository;
        this.creditTransactionRepository = creditTransactionRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public AdminStatsResponse stats() {
        List<User> customers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.USER).toList();
        List<Order> orders = orderRepository.findAll();
        List<CreditTransaction> credit = creditTransactionRepository.findAll();

        long pendingUsers = customers.stream().filter(u -> u.getStatus() == UserStatus.PENDING).count();
        long approvedUsers = customers.stream().filter(u -> u.getStatus() == UserStatus.APPROVED).count();
        BigDecimal totalCredit = customers.stream()
                .map(User::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingPayments = paymentRequestRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING).count();
        long pendingOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING
                || o.getStatus() == OrderStatus.APPROVED || o.getStatus() == OrderStatus.PROCESSING).count();
        long completedOrders = orders.stream().filter(o -> o.getStatus() == OrderStatus.COMPLETED).count();

        // Last 14 days, oldest first, including empty days
        LocalDate today = LocalDate.now();
        List<DailyPoint> daily = new ArrayList<>();
        for (int i = DAYS - 1; i >= 0; i--) {
            LocalDate day = today.minusDays(i);

            long ordersCount = orders.stream()
                    .filter(o -> o.getCreatedAt().toLocalDate().equals(day)).count();
            BigDecimal added = sumCredit(credit, day, CreditTransactionType.ADDED);
            BigDecimal used = sumCredit(credit, day, CreditTransactionType.USED);
            long registered = customers.stream()
                    .filter(u -> u.getCreatedAt() != null && u.getCreatedAt().toLocalDate().equals(day)).count();

            daily.add(new DailyPoint(day.toString(), ordersCount, added, used, registered));
        }

        // Quantity sold per product (by the English product name stored with each order line)
        Map<String, Long> sold = new HashMap<>();
        orders.stream()
                .filter(o -> o.getStatus() != OrderStatus.REJECTED && o.getStatus() != OrderStatus.CANCELLED)
                .forEach(o -> o.getItems().forEach(i -> {
                    String name = i.getProductName() != null && i.getProductName().get("en") != null
                            ? i.getProductName().get("en") : "Unknown";
                    sold.merge(name, (long) i.getQuantity(), Long::sum);
                }));
        List<NamedValue> productActivity = sold.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(6)
                .map(e -> new NamedValue(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        return new AdminStatsResponse(
                customers.size(),
                pendingUsers,
                approvedUsers,
                totalCredit,
                pendingPayments,
                orders.size(),
                pendingOrders,
                completedOrders,
                productRepository.count(),
                daily,
                productActivity);
    }

    private static BigDecimal sumCredit(List<CreditTransaction> all, LocalDate day, CreditTransactionType type) {
        return all.stream()
                .filter(t -> t.getType() == type && t.getCreatedAt().toLocalDate().equals(day))
                .map(CreditTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
