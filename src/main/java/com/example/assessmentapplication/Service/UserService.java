package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repo;

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
