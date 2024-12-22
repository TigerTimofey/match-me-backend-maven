package com.example.jwt_demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.multipart.MultipartFile;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.mapper.UserMapper;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.service.UserProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileService userProfileService;

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User deleted successfully!";
        } else {
            return "Error: User not found!";
        }
    }

    @PatchMapping("/{id}")
    public User updateUserById(
            @PathVariable Long id,
            @RequestParam("data") String userBioData,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> updates = objectMapper.readValue(userBioData, new TypeReference<Map<String, Object>>() {
            });

            if (image != null && !image.isEmpty() && !updates.containsKey("image")) {
                byte[] imageBytes = image.getBytes();
                updates.put("image", imageBytes);
            }

            // Если новые name или lastname приходят из фронта, обновляем их
            User currentUser = userProfileService.getCurrentUser();
            if (updates.containsKey("name")) {
                currentUser.setName((String) updates.get("name"));
            } else {
                updates.put("name", currentUser.getName());
            }
            if (updates.containsKey("lastname")) {
                currentUser.setLastname((String) updates.get("lastname"));
            } else {
                updates.put("lastname", currentUser.getLastname());
            }

            return userProfileService.updateUser(id, updates);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data", e);
        }
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("image", user.getImage());

        return response;
    }

    @GetMapping("/me")
    public User getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        return null;
    }

    @GetMapping("/me/profile")
    public UserProfileDTO getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username);
            return userMapper.toProfileDTO(user);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    // For /users/{id}/profile
    @GetMapping("/{id}/profile")
    public UserProfileDTO getUserProfileById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return userMapper.toProfileDTO(user);
    }

    @GetMapping("/{id}/bio")
    public Map<String, Object> getUserBioById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("name", user.getName());
        response.put("lastname", user.getLastname());
        response.put("city", user.getCity());
        response.put("age", user.getAge());
        response.put("genres", user.getGender());
        response.put("hobbies", user.getHobbies());

        return response;
    }

    @GetMapping("/me/bio")
    public Map<String, Object> getMyBio() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username);

            if (user == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No user");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("name", user.getName());
            response.put("lastname", user.getLastname());
            response.put("city", user.getCity());
            response.put("age", user.getAge());
            response.put("gender", user.getGender());
            response.put("hobbies", user.getHobbies());

            return response;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Unauthorized user - invalid or missing Bearer token");
    }
}