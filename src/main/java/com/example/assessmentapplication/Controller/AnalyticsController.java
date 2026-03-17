package com.example.assessmentapplication.Controller;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.TimeZoneStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assessmentapplication.Service.SubscriptionService;
import com.example.assessmentapplication.entity.Subscription;

@RequestMapping("api/analytics")
@RestController() // Sets the bean name
public class AnalyticsController {
    private final SubscriptionService subscriptionService;

    public AnalyticsController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/upcomingRenewals/{userId}")
    public List<Subscription> getUpcomingRenewals(@PathVariable int userId) {
        return subscriptionService.getUpcomingRenewals(userId);
    }

    @GetMapping("/totalAmount/{userId}")
    public java.math.BigDecimal getTotalAmount(@PathVariable int userId) {
        return subscriptionService.calculateTotalAmount(userId);
    }

}
