package com.example.assessmentapplication.integration;

import com.example.assessmentapplication.Repository.SubscriptionRepository;
import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.Subscription;
import com.example.assessmentapplication.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class SubscriptionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    public void setUp() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testCreateSubscription() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setServiceName("Netflix");
        subscription.setPlanType("Premium");
        subscription.setAmount(new BigDecimal("15.99"));
        subscription.setNextRenewalDate(LocalDate.now().plusMonths(1));

        mockMvc.perform(post("/api/subscriptions/user/" + testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(subscription)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceName").value("Netflix"))
                .andExpect(jsonPath("$.user").exists()); // Should show user since it's returned
    }

    @Test
    public void testGetAllSubscriptions() throws Exception {
        Subscription s1 = new Subscription();
        s1.setUser(testUser);
        s1.setServiceName("Spotify");
        s1.setPlanType("Premium");
        s1.setAmount(new BigDecimal("9.99"));
        s1.setNextRenewalDate(LocalDate.now());
        subscriptionRepository.save(s1);

        Subscription s2 = new Subscription();
        s2.setUser(testUser);
        s2.setServiceName("Hulu");
        s2.setPlanType("Standard (Ads)");
        s2.setAmount(new BigDecimal("7.99"));
        s2.setNextRenewalDate(LocalDate.now());
        subscriptionRepository.save(s2);

        mockMvc.perform(get("/api/subscriptions/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testDeleteSubscription() throws Exception {
        Subscription s1 = new Subscription();
        s1.setUser(testUser);
        s1.setServiceName("Disney+");
        s1.setPlanType("Basic");
        s1.setAmount(new BigDecimal("8.99"));
        s1.setNextRenewalDate(LocalDate.now());
        s1 = subscriptionRepository.save(s1);

        mockMvc.perform(delete("/api/subscriptions/" + s1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/subscriptions/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
