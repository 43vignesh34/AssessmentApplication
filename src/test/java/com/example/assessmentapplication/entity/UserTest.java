package com.example.assessmentapplication.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.PropertyValueException;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
        assertThrows(PropertyValueException.class, () -> {
        entityManager.persist(user);
        entityManager.flush();
    });
    }


}
