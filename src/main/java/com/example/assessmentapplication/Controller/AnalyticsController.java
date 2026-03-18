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
        log.info("Fetching upcoming renewals for user ID: {}", userId);
        return subscriptionService.getUpcomingRenewals(userId);
    }

    @GetMapping("/totalAmount/{userId}")
    public java.math.BigDecimal getTotalAmount(@PathVariable int userId) {
        log.info("Calculating total subscription amount for user ID: {}", userId);
        return subscriptionService.calculateTotalAmount(userId);
    }

}
