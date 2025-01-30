package com.example.jwt_demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jwt_demo.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.bio LEFT JOIN FETCH u.profile WHERE u.id = :id")
    Optional<User> findById(@Param("id") Long id);

}