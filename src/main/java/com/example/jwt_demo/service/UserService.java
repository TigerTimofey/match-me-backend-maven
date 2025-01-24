package com.example.jwt_demo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt_demo.model.Bio;
import com.example.jwt_demo.model.Profile;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public Bio findBioById(Long id) {
        User user = findById(id);
        return user != null ? user.getBio() : null;
    }

    public Profile findProfileById(Long id) {
        User user = findById(id);
        return user != null ? user.getProfile() : null;
    }

    public List<User> getRecommendations(String username) {
        User currentUser = findByUsername(username);
        if (currentUser == null) {
            return List.of();
        }

        return userRepository.findAll().stream()
            .filter(user -> !user.getUsername().equals(username))
            .filter(user -> !currentUser.getDismissed().contains(user.getId()))
            .filter(user -> !currentUser.getConnections().contains(user.getId()))
            .filter(user -> !currentUser.getOutcomeRequests().contains(user.getId()))
            .filter(user -> !currentUser.getIncomeRequests().contains(user.getId()))
            .collect(Collectors.toList());
    }

    public List<User> getConnections(String username) {
        User user = findByUsername(username);
        if (user == null) {
            return List.of();
        }

        return user.getConnections().stream()
            .map(id -> findById(id.longValue()))
            .filter(connection -> connection != null)
            .collect(Collectors.toList());
    }
} 