# Spring Security & JWT Concepts (Q&A)

## 1. Why do we need Spring Security? 
By default, any REST API you build is completely public. That means anyone on the internet who discovers your API endpoint (e.g., `http://your-aws-url.com/users/1`) can send a `GET` or `DELETE` request via curl or Postman and interact with your database directly.

Spring Security acts as a **Bouncer** for your entire application. Before any HTTP request reaches your `@RestController`, Spring Security intercepts it and enforces two core concepts:
1.  **Authentication (Who are you?):** Verifying the user exists and has provided valid credentials.
2.  **Authorization (What are you allowed to do?):** Making sure a standard "USER" cannot access admin-only endpoints.

## 2. What is a JWT (JSON Web Token)?
Historically, web applications used "Sessions". The server would save a Session ID in its RAM, give the user a cookie with that ID, and check its RAM on every single request. This breaks down when you scale up to multiple servers behind a load balancer, because Server B doesn't share Server A's RAM.

A **JWT** solves this through *Stateless Authentication*.
1.  **Login:** You send your credentials to the server.
2.  **The VIP Badge (JWT generation):** The server verifies your password, grabs a highly secure "Signing Key", and generates an encrypted token (the JWT). The server writes your identity directly into the payload of this token (e.g., `"username": "vignesh", "role": "ADMIN"`).
3.  **Stateless:** The server hands the JWT back to your frontend and *completely forgets about you*. It saves nothing in RAM.
4.  **Verification:** On your next API request, you attach this JWT to the HTTP `Authorization` header. Spring Security intercepts the request, uses the signing key to verify the token hasn't been forged or tampered with by a hacker, reads your identity from the payload, and lets you through. 

## 3. How do we implement it in Spring Boot?
Spring Boot Security does **not** support JWT out of the box. We must build the integration ourselves using three components:
1.  **`spring-boot-starter-security`**: The core bouncer engine.
2.  **`jjwt` libraries**: The tools needed to generate, sign, and parse the JSON Web Tokens.
3.  **Custom Filters**: We have to write a custom Java class (`JwtAuthenticationFilter`) that sits in front of the API, looks for the token in the HTTP header, and checks its validity.

## 4. Why do we need the `UserDetails` interface?
Spring Security is a massive, generic framework used by millions of companies. It is completely blind to your custom Java Code—it doesn't understand your Database, your `User` table, or whether you use `username` or `email` to log in.

The `UserDetails` interface acts as a **Universal Translator**. By attaching `implements UserDetails` to your custom `User` entity, you are signing a contract with Spring Security. The framework forces you to override 5 specific methods (like `getPassword()`, `getUsername()`, and `getAuthorities()`). 

When a user tries to log in, Spring Security ignores the rest of your custom code and calls these 5 standardized methods to extract the exact security parameters it needs to authenticate the user, regardless of how your database is structured underneath.

## 5. What are Java Enums and why use them for Roles (like ADMIN/USER)?
An `Enum` (short for Enumeration) is a special Java feature that represents a fixed, unchanging list of constants. Think of it like a strict Dropdown Menu on a website where you cannot type custom text.

If you stored user roles as simple Java `String`s, someone might accidentally type `user.setRole("ADMN")` in the code instead of `"ADMIN"`. Because it's a valid string, the Java compiler will compile it successfully, but the code will silently crash in production because the role `"ADMN"` doesn't have permissions to do anything.

By creating an `Enum` with exactly two options (`USER, ADMIN`), the Java compiler will instantly throw a red error line if another developer tries to type anything else. It prevents typos and makes your security code bulletproof.

***Note on Databases:** When using Hibernate/JPA, Java Enums are saved as numbers in the database by default (`USER` = 0, `ADMIN` = 1). By adding the `@Enumerated(EnumType.STRING)` annotation above the field in your Entity, you command Hibernate to save the literal word "USER" or "ADMIN" as text in the SQL table for readability.*

## 6. What is the `UserDetailsService` and `CustomUserDetailsService`?
Even after you implement the `UserDetails` contract on your database entity, Spring Security still doesn't know *where* your users are stored (MySQL, Postgres, Active Directory, etc.).

The `UserDetailsService` is the second interface you must implement to build the complete bridge. You create a `CustomUserDetailsService` class, inject your Spring Data `UserRepository`, and write exactly one method: `loadUserByUsername(String username)`.

When a user types "vignesh" into the login screen and hits submit, Spring Security internally invokes your `loadUserByUsername("vignesh")` method. It is your job to write the Java code that queries the database (`repository.findByUsername()`), formats it as a `UserDetails` object, and hands it back to Spring Security so the Bouncer can verify the password.
