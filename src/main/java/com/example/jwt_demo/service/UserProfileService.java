package com.example.jwt_demo.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    public UserProfileDTO getUserProfile(Long id) {
        User user = findUserById(id);
        return convertToProfileDTO(user);
    }

    public User updateUser(Long id, Map<String, Object> updates) {
        User user = findUserById(id);
        applyUpdates(user, updates);
        return userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        // Заполнить DTO данными из user
        return dto;
    }

    private void applyUpdates(User user, Map<String, Object> updates) {
        // Логика обновления полей пользователя
    }
}
