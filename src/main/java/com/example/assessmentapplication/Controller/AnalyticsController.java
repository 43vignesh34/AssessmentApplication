package com.example.assessmentapplication.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assessmentapplication.Service.SubscriptionService;
import com.example.assessmentapplication.entity.Subscription;

@RequestMapping("api/analytics")
@RestController() // Sets the bean name
public class AnalyticsController {
    @Autowired
    SubscriptionService subscriptionService;

    @GetMapping("/upcomingRenewals/{userId}")
    public List<Subscription> getUpcomingRenewals(@PathVariable int userId) {
        return subscriptionService.getUpcomingRenewals(userId);
    }

}
