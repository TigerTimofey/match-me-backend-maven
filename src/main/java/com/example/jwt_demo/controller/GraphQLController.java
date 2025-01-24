package com.example.jwt_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.example.jwt_demo.model.Bio;
import com.example.jwt_demo.model.Profile;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.service.UserService;

@Controller
public class GraphQLController {

    @Autowired
    private UserService userService;

    @QueryMapping
    public User user(@Argument String id) {
        return userService.findById(Long.parseLong(id));
    }

    @QueryMapping
    public Bio bio(@Argument String id) {
        return userService.findBioById(Long.parseLong(id));
    }

    @QueryMapping
    public Profile profile(@Argument String id) {
        return userService.findProfileById(Long.parseLong(id));
    }

    @QueryMapping
    public User me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.findByUsername(auth.getName());
    }

    @QueryMapping
    public Bio myBio() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return user.getBio();
    }

    @QueryMapping
    public Profile myProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByUsername(auth.getName());
        return user.getProfile();
    }

    @QueryMapping
    public List<User> recommendations() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getRecommendations(auth.getName());
    }

    @QueryMapping
    public List<User> connections() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userService.getConnections(auth.getName());
    }

    @SchemaMapping
    public Bio bio(User user) {
        return user.getBio();
    }

    @SchemaMapping
    public Profile profile(User user) {
        return user.getProfile();
    }

    @SchemaMapping
    public User user(Bio bio) {
        return bio.getUser();
    }

    @SchemaMapping
    public User user(Profile profile) {
        return profile.getUser();
    }
} 