package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.Repository.SubscriptionRepository;
import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.Subscription;
import com.example.assessmentapplication.entity.User;
import com.example.assessmentapplication.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * KEY CONCEPT: Lazy vs Eager Evaluation
 * 
 * When using Optional.orElseThrow(), we use a lambda () -> new Exception()
 * instead of just new Exception().
 * 
 * - Eager (without lambda): The exception object is created IMMEDIATELY, even
 * if the value exists. This wastes memory.
 * - Lazy (with lambda): The exception is created ONLY if the value is missing.
 * This is more efficient.
 */
@Service
public class SubscriptionService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public Subscription createSubscription(int userId, Subscription subscription) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        subscription.setUser(user);
        return subscriptionRepository.save(subscription); // The save method is a built-in method of JpaRepository. It
                                                          // inserts a new row if the ID is null, or updates an existing
                                                          // row if the ID is present.
    }

    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    public Subscription getSubscriptionById(int id) {
        // Don't use Integer as the parameter type for id, because if the id is null, it
        // will throw a NullPointerException
        return subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found!"));
    }

    public Subscription updateSubscription(int id, Subscription subscriptionDetails) {
        Subscription presentSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found!"));
        presentSubscription.setServiceName(subscriptionDetails.getServiceName());
        presentSubscription.setPlanType(subscriptionDetails.getPlanType());
        presentSubscription.setNextRenewalDate(subscriptionDetails.getNextRenewalDate());
        presentSubscription.setAmount(subscriptionDetails.getAmount());
        presentSubscription.setCurrency(subscriptionDetails.getCurrency());

        return subscriptionRepository.save(presentSubscription);
    }

    public void deleteById(int id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("Subscription not found!");
        }
    }

    public List<Subscription> getSubscriptionByUserId(int userid) {
        return subscriptionRepository.findByUserId(userid);
    }

    public List<Subscription> getUpcomingRenewals(int userId) {
        LocalDate localDate = LocalDate.now(); // Gets todays date
        LocalDate endDate = localDate.plusDays(7);
        return subscriptionRepository.findByUserIdAndNextRenewalDateBetween(userId, localDate, endDate);
    }

    public java.math.BigDecimal calculateTotalAmount(int userId) {
        return subscriptionRepository.calculateAmountForUser(userId);
    }

}
