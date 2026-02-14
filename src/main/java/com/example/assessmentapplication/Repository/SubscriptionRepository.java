package com.example.assessmentapplication.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.assessmentapplication.entity.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer>
// Subscription is the entity class and Integer is the type of the primary key
{
    // Find by Id method not needed because JpaRepository already provides
    // it(Returns Optional<Subscription>)

    // Derived Query Method(Spring takes care of generating the SQL query)
    List<Subscription> findByUserId(int userid); // Just method definition is enough as Spring Data JPA will
                                                 // automatically generate the query

    List<Subscription> findByUserIdAndNextRenewalDateBetween(int userid, LocalDate startDate, LocalDate endDate);
}
