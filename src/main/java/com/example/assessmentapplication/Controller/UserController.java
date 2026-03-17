package com.example.assessmentapplication.Controller;

import com.example.assessmentapplication.Service.UserService;
import com.example.assessmentapplication.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/getUser")
    public User getUser(@RequestParam("username") String username) {
        log.info("Fetching user with username: {}", username);
        return userService.findByUsername(username);
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody @Valid User user) {
        userService.registerUser(user);
    }
}
