package com.example.assessmentapplication.Service;

//It implements the Spring Security interface UserDetailsService

import org.springframework.stereotype.Service;

import com.example.assessmentapplication.Repository.UserRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repo;

    @Override // Optional annotation. But best practice.
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Spring Security attempting to load user details for: {}", username);
        return repo.findByUsername(username).orElseThrow(() -> {
            log.warn("Login attempt failed: User '{}' not found", username);
            return new UsernameNotFoundException("User not found");
        });

    }

}