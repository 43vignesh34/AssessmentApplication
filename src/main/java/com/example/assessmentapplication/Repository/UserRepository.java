 package com.example.assessmentapplication.Repository;

import com.example.assessmentapplication.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer>
{
    User findByUsername(String username);
}