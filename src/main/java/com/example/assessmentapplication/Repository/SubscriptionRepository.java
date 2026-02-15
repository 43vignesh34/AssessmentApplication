package com.example.assessmentapplication.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query("SELECT COALESCE(SUM(s.amount),0) FROM Subscription s WHERE s.user.id = :userId")
    // COALESCE is a SQL function that returns the first non-null value in the list
    // of arguments
    // :userId is a named parameter
    BigDecimal calculateAmountForUser(@Param("userId") int userid);
}
