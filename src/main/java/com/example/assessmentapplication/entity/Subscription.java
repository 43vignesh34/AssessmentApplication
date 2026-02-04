package com.example.assessmentapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
@Getter
@Setter
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Link to the User (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY) // Many subscriptions can belong to one user
    @JoinColumn(name = "user_id", nullable = false) // Creates a column in the subscriptions table called user_id
    private User user;

    // 2. Service Name (e.g., "Netflix", "Spotify")
    @Column(nullable = false)
    private String serviceName;

    // 3. Plan Type (e.g., "Premium 4K", "Student")
    private String planType;

    // 4. Renewal Date (e.g., 2023-10-25)
    @Column(name = "next_renewal_date")
    private LocalDate nextRenewalDate;

    // 5. Cost (e.g., 15.99)
    @NotNull
    private BigDecimal amount; // When we do calculations, we use BigDecimal instead of double because double
                               // is not precise

    private String currency = "USD"; // Default to USD
}
