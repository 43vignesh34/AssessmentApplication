package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
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
        log.debug("Fetching user with username: {}", username);
        User user = repo.findByUsername(username).orElse(null);
        log.debug("User found: {}", user);
        return user;
    }

    public void registerUser(User user) {
        repo.save(user);
    }

}
