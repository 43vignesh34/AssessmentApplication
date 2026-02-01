# Application Request Flow Analysis

## 1. Scenario: Good Input (Success)
**Input Data**: `{"username": "vignesh", "password": "password123"}`

1.  **Request Entry (DispatcherServlet)**
    *   The HTTP POST request `/register` arrives at the Spring Boot server.
    *   The `DispatcherServlet` (Front Controller) finds that the `Controller.registerUser` method handles this URL.

2.  **Input Validation (Controller Layer)**
    *   Before executing the method, Spring sees the `@Valid` annotation on the `User` parameter.
    *   It inspects the `User` object constraints (specifically `@NotBlank` on `username`).
    *   **Check:** Is "vignesh" blank? **No.**
    *   **Result:** Validation **PASSES**. The `User` object is fully constructed.

3.  **Business Logic (Service Layer)**
    *   The `Controller` calls `userService.registerUser(user)`.
    *   The `UserService` executes any business logic (currently just calling the repository).

4.  **Data Persistence (Repository Layer)**
    *   The `UserService` calls `userRepository.save(user)`.
    *   Hibernate (the JPA provider) translates this into a SQL `INSERT` statement.
    *   The data is written to the H2 database.

5.  **Response (Success)**
    *   The method returns `void`.
    *   Spring automatically sends back an HTTP **200 OK** response to the client.

---

## 2. Scenario: Bad Input (Failure)
**Input Data**: `{"username": "", "password": "password123"}`

1.  **Request Entry (DispatcherServlet)**
    *   The HTTP POST request sends the JSON payload with a blank username.

2.  **Input Validation (Controller Layer)**
    *   Spring attempts validation due to `@Valid`.
    *   It checks `@NotBlank` on the empty string `""`.
    *   **Check:** Is "" blank? **Yes.**
    *   **Result:** Validation **FAILS**.
    *   **Action:** Spring **stops execution immediately**. The `registerUser` method in the `Controller` is **NEVER** called.
    *   **Exception:** Spring throws a `MethodArgumentNotValidException`.

3.  **Exception Handling (Global Advice Layer)**
    *   The exception bubbles up.
    *   The `GlobalExceptionHandler` (annotated with `@RestControllerAdvice`) is watching for this specific exception.
    *   The `handleValidationExceptions` method intercepts the error.

4.  **Error Formatting**
    *   The handler extracts the field (`username`) and the message (`must not be blank`) from the exception.
    *   It constructs a JSON map: `{"username": "must not be blank"}`.

5.  **Response (Client Error)**
    *   The handler returns a `ResponseEntity` with this JSON and explicitly sets the status to **400 Bad Request**.
    *   The client receives the 400 error, understanding that *their* input was wrong, not that the server crashed.
