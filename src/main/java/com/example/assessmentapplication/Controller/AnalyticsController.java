package com.example.assessmentapplication.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assessmentapplication.Service.SubscriptionService;
import com.example.assessmentapplication.entity.Subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("api/analytics")
@RestController() // Sets the bean name
@RequiredArgsConstructor
@Slf4j
public class AnalyticsController {
    private final SubscriptionService subscriptionService;

    @GetMapping("/upcomingRenewals/{userId}")
    public List<Subscription> getUpcomingRenewals(@PathVariable int userId) {
        log.info("[ANALYTICS] Request received: Fetching upcoming renewals for user ID: {}", userId);
        long startTime = System.currentTimeMillis();

        List<Subscription> result = subscriptionService.getUpcomingRenewals(userId);

        long duration = System.currentTimeMillis() - startTime;
        log.info("[ANALYTICS] Request completed in {}ms. Found {} renewals for user: {}", duration, result.size(),
                userId);
        return result;
    }

    @GetMapping("/totalAmount/{userId}")
    public java.math.BigDecimal getTotalAmount(@PathVariable int userId) {
        log.info("[ANALYTICS] Request received: Calculating total subscription amount for user ID: {}", userId);
        long startTime = System.currentTimeMillis();

        java.math.BigDecimal amount = subscriptionService.calculateTotalAmount(userId);

        long duration = System.currentTimeMillis() - startTime;
        log.info("[ANALYTICS] Request completed in {}ms. Total amount for user {}: {}", duration, userId, amount);
        return amount;
    }

}
