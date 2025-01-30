package com.example.jwt_demo.controller;

import com.example.jwt_demo.model.Profile;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.Bio;
import com.example.jwt_demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GraphQLController {

    @Autowired
    private UserRepository userRepository;

    // Get User by ID
    @QueryMapping
    public User user(@Argument Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get Bio by ID
    @QueryMapping
    public Bio bio(@Argument Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getBio();
    }

    // Get Profile by ID
    @QueryMapping
    public Profile profile(@Argument Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getProfile();
    }

    // Get current user (me)
    @QueryMapping
    public User me(@Argument Long id) {
        // You can replace this with the logic to get the authenticated user if needed
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // Get current user's Bio (myBio)
    @QueryMapping
    public Bio myBio(@Argument Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getBio();
    }

    // Get current user's Profile (myProfile)
    @QueryMapping
    public Profile myProfile(@Argument Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getProfile();
    }

    // Get User Recommendations (you can define your own logic for recommendations)
    @QueryMapping
    public List<User> recommendations(@Argument Long id) {
        // Replace this logic with your own to fetch recommendations
        return userRepository.findAll(); // This is a placeholder for your recommendation logic
    }

    // Get User Connections (you can define your own logic for connections)
    @QueryMapping
    public List<User> connections(@Argument Long id) {
        // Replace this logic with your own to fetch user connections
        return userRepository.findAll(); // This is a placeholder for your connection logic
    }
}
