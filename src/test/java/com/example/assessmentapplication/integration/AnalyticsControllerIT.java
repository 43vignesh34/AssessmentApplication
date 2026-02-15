package com.example.assessmentapplication.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test; // For the @Test annotation
import org.springframework.beans.factory.annotation.Autowired; // To inject dependencies
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Setup MockMvc
import org.springframework.boot.test.context.SpringBootTest; // Load the full application context
import org.springframework.test.web.servlet.MockMvc; // Simulate HTTP requests
import com.example.assessmentapplication.Repository.SubscriptionRepository;
import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.Subscription;
import com.example.assessmentapplication.entity.User;

@SpringBootTest // "Hey Spring, start up the whole application for this test!"
@AutoConfigureMockMvc // "Also, please set up a fake browser (MockMvc) for us to use."
public class AnalyticsControllerIT {

    @Autowired
    private MockMvc mockMvc; // This is our fake browser. We'll use it to send GET requests.

    @Autowired
    UserRepository userRepository;

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Test
    @SuppressWarnings({ "null" }) // Didnt understand this
    public void testGetUpcomingRenewals() throws Exception {
        User user = new User();
        user.setUsername("John Doe");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        Subscription subscription1 = new Subscription();
        subscription1.setUser(savedUser);
        subscription1.setNextRenewalDate(LocalDate.now().plusDays(1));
        subscription1.setServiceName("Netflix");
        subscription1.setPlanType("Premium");
        subscription1.setAmount(new BigDecimal(299.0));
        subscription1.setCurrency("INR");
        subscriptionRepository.save(subscription1);

        Subscription subscription2 = new Subscription();
        subscription2.setUser(savedUser);
        subscription2.setNextRenewalDate(LocalDate.now().plusDays(20));
        subscription2.setServiceName("Prime Video");
        subscription2.setPlanType("Premium");
        subscription2.setAmount(new BigDecimal(199.0));
        subscription2.setCurrency("INR");
        subscriptionRepository.save(subscription2);

        mockMvc.perform(get("/api/analytics/upcomingRenewals/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1))).andExpect(jsonPath("$[0].serviceName", is("Netflix")));

    }

    @SuppressWarnings("null") // Why do we need this?
    @Test
    public void testGetTotalAmountForUser() throws Exception // Why do we need this?
    {
        User user = new User();
        user.setUsername("John Doe");
        user.setPassword("password");
        User savedUser = userRepository.save(user);

        Subscription subscription1 = new Subscription();
        subscription1.setUser(savedUser);
        subscription1.setNextRenewalDate(LocalDate.now().plusDays(1));
        subscription1.setServiceName("Netflix");
        subscription1.setPlanType("Premium");
        subscription1.setAmount(new BigDecimal(10.0));
        subscription1.setCurrency("INR");
        subscriptionRepository.save(subscription1);

        Subscription subscription2 = new Subscription();
        subscription2.setUser(savedUser);
        subscription2.setNextRenewalDate(LocalDate.now().plusDays(20));
        subscription2.setServiceName("Prime Video");
        subscription2.setPlanType("Premium");
        subscription2.setAmount(new BigDecimal(20.0));
        subscription2.setCurrency("INR");
        subscriptionRepository.save(subscription2);

        mockMvc.perform(get("/api/analytics/totalAmount/" + savedUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(30.0)));
    }

}
