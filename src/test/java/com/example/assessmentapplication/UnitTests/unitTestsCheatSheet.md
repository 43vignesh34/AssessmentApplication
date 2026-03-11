# Pure Unit Testing Cheat Sheet (JUnit 5 + Mockito)

Unit tests isolate a single class to verify its logic. **We do NOT start the Spring Context** (no `@SpringBootTest`, no `@Autowired`, no database). We run tests instantly using pure Java and Mockito.

## 1. Class-Level Annotations
```java
@ExtendWith(MockitoExtension.class) // Required: Tells JUnit 5 to process @Mock and @InjectMocks
class UserServiceTest { ... }
```
> [!WARNING]
> If you forget `@ExtendWith(MockitoExtension.class)`, Mockito will not turn on. All your `@Mock` variables will remain `null`, and your tests will crash with a `NullPointerException` (NPE) as soon as you try to stub or call them!

## 2. Setting Up the Test Subject
*   **`@Mock`**: Creates a fake, hollow version of a dependency (like a Repository). If you call a method on it, it returns `null` or `0` by default.
*   **`@InjectMocks`**: Creates an instance of the *real* class you are testing and automatically pushes your `@Mock` objects into its constructor.

```java
@Mock
private UserRepository userRepository;

// The ACTUAL class we are testing. 
// Mockito automatically does: new UserService(userRepository)
@InjectMocks 
private UserService userService;
```

## 3. The 3 A's of Testing
Every pure unit test should follow this structure:
1.  **Arrange:** Set up test data and teach your mocks how to answer questions (`when(...)`).
2.  **Act:** Actually call the method you want to test on the `@InjectMocks` class.
3.  **Assert:** Check the return value (`assertEquals`) and verify the mock was used correctly (`verify`).

## 4. Teaching Mocks How to Behave (Stubbing)
You must explicitly tell your `@Mock`s what to return when the class under test calls them.

**Return a specific value:**
```java
when(userRepository.findByUsername("alice")).thenReturn(Optional.of(mockUser));
```
> [!IMPORTANT]
> **Data Types Matter:** The value inside `thenReturn()` must perfectly match the method's return type. If a method returns a `long`, you must return `0L` (not `0`, which is an `int`). Otherwise, Mockito will throw a `WrongTypeOfReturnValue` error or fail to compile.

**Return different values on consecutive calls:**
```java
when(mockList.size()).thenReturn(1, 2, 3); // Returns 1 first time, 2 second time, etc.
```

**Throw an Exception:**
```java
when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("DB Down"));
```

**Stubbing Void Methods (doX vs whenX):**
For methods that return `void` (they don't return anything), you must swap the standard syntax:
```java
doNothing().when(mockEmailService).sendEmail(anyString());
doThrow(new RuntimeException()).when(mockRepository).deleteById(anyLong());
```

## 5. Mockito Matchers (`any()`)
If you don't care about the *exact* input parameter a mock receives, use matchers:
*   `any()` or `any(User.class)`: Any object of that type
    *   *Why `.class`?* Java methods cannot accept a conceptual blueprint ("User") as an argument, they only accept concrete objects. passing `User.class` hands Mockito the concrete `Class` object so it knows exactly what type to look for on the clipboard!
*   `anyString()`, `anyLong()`, `anyInt()`: Any primitive
*   `eq(5L)`: Must be exactly this long value (useful when combining with `any()`)

*Rule: If you use a matcher for ONE argument, you must use it for ALL arguments in that method call.* 
*(e.g., `when(service.doThing(anyString(), eq(5)))`)*

## 6. Proving the Mock Was Used (Verification)
Sometimes a method returns `void`, so there's nothing to `assertEquals` against. Instead, you verify the mock did its job.

```java
// Verify a method was called EXACTLY once
verify(userRepository, times(1)).save(any(User.class));

// Verify a method was NEVER called
verify(userRepository, never()).deleteById(anyLong());

// Verify no OTHER methods were called on this mock
verifyNoMoreInteractions(userRepository);
```

## 7. Common Assertions (JUnit 5)
```java
import static org.junit.jupiter.api.Assertions.*;

assertEquals(expected, actual);
assertNotEquals(unexpected, actual);
assertTrue(condition);
assertFalse(condition);
assertNull(object);
assertNotNull(object);

// Testing if an exception was thrown correctly by your business logic
assertThrows(IllegalArgumentException.class, () -> {
    userService.findUserById(-1L);
});
```

## 8. Complete Unit Test Example
```java
package com.example.assessmentapplication.Service;

import com.example.assessmentapplication.entity.User;
import com.example.assessmentapplication.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testFindUser_WhenUserExists() {
        // 1. Arrange
        User expectedUser = new User();
        expectedUser.setUsername("alice");
        
        // Teach the mock to return Alice when asked
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(expectedUser));

        // 2. Act
        User actualUser = userService.findUserByUsername("alice");

        // 3. Assert
        assertNotNull(actualUser);
        assertEquals("alice", actualUser.getUsername());
        
        // Prove the mock was actually touched
        verify(userRepository, times(1)).findByUsername("alice"); 
    }
}

## 9. Understanding "Dumb" Mocks: Stubbing vs Verifying
A very common point of confusion is wondering how a mock can do anything if you don't stub it first.

Mocks are completely "dumb". By default, every method on a mock does absolutely nothing and returns `null` (or `0` / `false`). 

If your business logic calls `userRepository.save(user)`, the mock blindly accepts the `user` object, throws it away, does nothing, and the code continues executing.

**So how do we test methods that return `void`?**
This is where the magic of Mockito comes in: **Mockito secretly logs every single interaction with a mock**, even the methods you didn't explicitly stub.

Think of a `@Mock` as a security guard with a clipboard:
*   **Stubbing (`when`)**: You give the guard a script. *"If someone asks `count()`, tell them 0."* You ONLY need to do this if your business logic actually uses the returned value to keep working.
*   **Executing (`service.createAdmin()`)**: The service hands an object to the `save()` method. The guard has no script for this, so they just take the object, throw it away, and say nothing.
*   **Verifying (`verify`)**: You check the guard's clipboard at the end of the test. *"Did anyone call `save()` with `any(User.class)` today?"*

Because Mockito logs *everything* on that clipboard automatically, we can use `verify()` to prove our code successfully attempted an action, even if the mock itself is dumb and didn't actually execute any real database logic.
```
