package com.example.assessmentapplication.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.assessmentapplication.Service.SubscriptionService;
import com.example.assessmentapplication.entity.Subscription;

@RestController
@RequestMapping("/api/subscriptions") // Changed to /api/subscriptions to match tests
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    // 1. Get All Subscriptions for a User
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Subscription>> getAllSubscriptions(@PathVariable int userId) {
        List<Subscription> subscriptions = subscriptionService.getSubscriptionByUserId(userId);
        return new ResponseEntity<>(subscriptions, HttpStatus.OK);
    }

    @PostMapping("/createSubscription/{userId}")
    public ResponseEntity<Subscription> createSubscription(@PathVariable int userId,
            @RequestBody Subscription subscription) {
        Subscription createdSubscription = subscriptionService.createSubscription(userId, subscription);
        return new ResponseEntity<>(createdSubscription, HttpStatus.CREATED);
    }

    @GetMapping("/subscription/{id}")
    public ResponseEntity<Subscription> getSubscriptionById(@PathVariable int id) {
        Subscription subscription = subscriptionService.getSubscriptionById(id);
        return new ResponseEntity<>(subscription, HttpStatus.OK);
    }

    @PutMapping("/updateSubscription/{id}")
    public ResponseEntity<Subscription> updateSubscription(@PathVariable int id, @RequestBody Subscription subscription)
    // id is also taken as input to verify if the subscription exists and we're
    // updating the correct subscription
    {
        Subscription updatedSubscription = subscriptionService.updateSubscription(id, subscription);
        return new ResponseEntity<>(updatedSubscription, HttpStatus.OK);
    }

    @DeleteMapping("/deleteSubscription/{id}")
    public ResponseEntity<String> deleteSubscription(@PathVariable int id) {
        subscriptionService.deleteById(id);
        return new ResponseEntity<>("Subscription deleted successfully", HttpStatus.OK);
    }
}
