package com.example.assessmentapplication.Repository;

import com.example.assessmentapplication.entity.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);

    Optional<User> findById(Integer userId); // Optional is a container object used to contain not-null objects. It
                                             // avoids NullLinkException and forces you to handle the case where the
                                             // value might be missing.
}