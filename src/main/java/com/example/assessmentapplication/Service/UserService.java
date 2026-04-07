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

    public UserService(UserRepository repo, MeterRegistry meterRegistry) {
        this.repo = repo;
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
            admin.setPassword("default");
            admin.setRole(com.example.assessmentapplication.entity.Role.ADMIN);
            repo.save(admin);
        }
    }

    public User findByUsername(String username) {
        log.debug("Fetching user by username: {}", username);
        return repo.findByUsername(username).orElse(null);
    }

    public void registerUser(User user) {
        // Why {} instead of +?
        // Parameterized logging (using {}) only builds the string if the log level is
        // active (e.g. INFO).
        // The + version (concatenation) always spends CPU time building the string,
        // even if the log is never printed.
        log.info("User successfully registered: {}", user.getUsername());
        userCounter.increment();
        repo.save(user);
    }

}
