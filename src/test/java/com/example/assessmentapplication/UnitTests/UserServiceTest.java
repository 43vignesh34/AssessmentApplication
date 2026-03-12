package com.example.assessmentapplication.UnitTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.assessmentapplication.Repository.UserRepository;
import com.example.assessmentapplication.Service.UserService;
import com.example.assessmentapplication.entity.Role;
import com.example.assessmentapplication.entity.User;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void createAdmin_WhenNoUsersExist() {
        // Arrange: userRepository.count() returns a 'long', so we must return '0L', not
        // '0' (int). count() isnt called yet, we prepare for it
        when(userRepository.count()).thenReturn(0L);

        // Act: Call the real service method
        userService.createAdmin();

        // Assert: Verify that the save method was called exactly 1 time.
        // We use any(User.class) to match any User object that was created internally
        // by the method.
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createAdmin_WhenUsersExist() {
        // Arrange: Simulate that 5 users already exist in the database
        when(userRepository.count()).thenReturn(5L);

        // Act: Call the real service method
        userService.createAdmin();

        // Assert: Verify that the save method was NEVER called.
        // never() is the exact same thing as times(0).
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByUsername_WhenUserExists() {
        User user1 = new User();
        user1.setUsername("Vignesh");
        user1.setPassword("password");
        user1.setRole(Role.USER);
        // Arrange
        when(userRepository.findByUsername("Vignesh")).thenReturn(Optional.of(user1));
        // Act
        User user2 = userService.findByUsername("Vignesh");
        // Assert
        assertEquals(user1, user2);
    }

    @Test
    void findByUsername_WhenUserDoesntExist() {
        // Arrange
        when(userRepository.findByUsername("Suresh")).thenReturn(null);
        // Act
        User user = userService.findByUsername("Suresh");
        // Assert
        assertNull(user);
    }

    @Test
    void registerUser_WhenUserIsRegistered() {
        // Arrange
        User user = new User();
        user.setUsername("Vignesh");
        user.setPassword("password");
        user.setRole(Role.USER);

        // Act
        userService.registerUser(user);

        // Assert
        verify(userRepository, times(1)).save(user);
    }
}