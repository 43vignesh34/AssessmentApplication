package com.example.assessmentapplication.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserTest 
{
    @PersistenceContext
    EntityManager entityManager;
    @Test
    void checkIdPositive()
    {
        User user = new User();
        user.setUsername("Vignesh Suresh");
        entityManager.persist(user);
        entityManager.flush();
        assertTrue(user.getId()>0);
    }
    @Test
    void checkUsernameIsNull()
    {
        User user = new User();
        user.setUsername(null);
        assertThrows(ConstraintViolationException.class, () -> {
        entityManager.persist(user);
        entityManager.flush();
    });
    }


}
