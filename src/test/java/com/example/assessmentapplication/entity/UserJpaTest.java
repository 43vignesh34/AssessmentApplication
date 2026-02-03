package com.example.assessmentapplication.entity;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest // Loads only the database layer and not the entire application
@AutoConfigureTestDatabase(replace = Replace.ANY) // forces replacement of the real database with an in-memory database
// Ensures that the tests run against an in-memory database without messing with
// the real database
class UserJpaTest {

    @PersistenceContext // Autowired but for JPA
    private EntityManager entityManager;

    @Test
    void showDatabase() {
        entityManager
                .createNativeQuery("select 1")
                .getSingleResult();
    }

    @Test
    void persistUser_generatesId_andCanBeReadBack() {
        User user = new User();
        user.setUsername("vignesh");
        user.setPassword("secret");

        entityManager.persist(user);
        entityManager.flush(); // forces insert now

        assertTrue(user.getId() > 0);

        User fromDb = entityManager.find(User.class, user.getId());
        assertNotNull(fromDb);
        assertEquals("vignesh", fromDb.getUsername());
        assertEquals("secret", fromDb.getPassword());
    }

    @Test
    void username_isRequired() {
        User user = new User();
        // user.setUsername("vignesh1");
        user.setPassword("secret");

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persist(user);
            entityManager.flush(); // keep flush to force it if persist doesn’t throw
        });
    }
}
