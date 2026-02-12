package com.example.assessmentapplication.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    private Integer id;

    // 1. Link to the User (Foreign Key)
    @ManyToOne(fetch = FetchType.LAZY) // Many subscriptions can belong to one user
    @JoinColumn(name = "user_id", nullable = false) // Creates a column in the subscriptions table called user_id
    private User user;

    // 2. Service Name (e.g., "Netflix", "Spotify")
    @Column(nullable = false)
    @NotBlank(message = "Service name is required")
    private String serviceName;

    // 3. Plan Type (e.g., "Premium 4K", "Student")
    @NotBlank(message = "Plan type is required")
    private String planType;

    // 4. Renewal Date (e.g., 2023-10-25)
    @Column(name = "next_renewal_date")
    private LocalDate nextRenewalDate;

    // 5. Cost (e.g., 15.99)
    @NotNull
    @Positive(message = "Value must be positive")
    private BigDecimal amount; // When we do calculations, we use BigDecimal instead of double because double
                               // is not precise

    @Getter
    private String currency = "USD"; // Default to USD
}
