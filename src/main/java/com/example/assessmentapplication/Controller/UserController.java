package com.example.assessmentapplication.Controller;

import com.example.assessmentapplication.Service.UserService;
import com.example.assessmentapplication.entity.User;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MeterRegistry meterRegistry;
    private Counter userCounter;

    @PostConstruct
    public void init() {
        this.userCounter = Counter.builder("user.count")
                .description("Number of users")
                .register(meterRegistry);
    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam("username") String username) {
        log.info("Fetching user with username: {}", username);
        return userService.findByUsername(username);
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid User user) {
        userService.registerUser(user);
        userCounter.increment();
    }
}
