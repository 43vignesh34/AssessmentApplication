package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.User;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

    private final UserRepository repo;
    private final Counter userCounter;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, MeterRegistry meterRegistry,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.userCounter = Counter.builder("user.registered.count")
                .description("Number of users registered")
                .register(meterRegistry);
    }

    @PostConstruct
    public void createAdmin() {
        if (repo.count() == 0) {
            log.info("No users found. Creating default admin...");
            User admin = new User();
            admin.setUsername("admin1");
            admin.setPassword(passwordEncoder.encode("default"));
            admin.setRole(com.example.assessmentapplication.entity.Role.ADMIN);
            repo.save(admin);
        }
    }

    public User findByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return repo.findByUsername(username).orElse(null);
    }

    public void registerUser(User user) {
        log.info("User successfully registered: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userCounter.increment();
        repo.save(user);
    }

}
