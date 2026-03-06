package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    @PostConstruct
    public void createAdmin() {
        if (repo.count() == 0) {
            System.out.println("No users found. Creating default admin...");
            User admin = new User();
            admin.setUsername("admin1");
            admin.setPassword("default");
            admin.setRole(com.example.assessmentapplication.entity.Role.ADMIN);
            repo.save(admin);
        }
    }

    public User findByUsername(String username) {
        System.out.println(">> username=\"" + username + "\" length=" + username.length());
        User user = repo.findByUsername(username).orElse(null);
        System.out.println("User:" + user);
        return user;
    }

    public void registerUser(User user) {
        repo.save(user);
    }

}
