package com.example.assessmentapplication.Controller;

import com.example.assessmentapplication.Service.UserService;
import com.example.assessmentapplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getUser")
    public User getUser(@RequestParam("username") String username) {
        System.out.println(username);
        return userService.findByUsername(username);
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid User user) {
        userService.registerUser(user);
    }
}
