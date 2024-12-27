package com.example.jwt_demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.exception.AccessDeniedException;
import com.example.jwt_demo.exception.ResourceNotFoundException;
import com.example.jwt_demo.mapper.UserMapper;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.service.UserProfileService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserProfileService userProfileService;
    private final PasswordEncoder encoder;

    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserProfileDTO> userDTOs = users.stream()
                .map(userMapper::toProfileDTO)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/generate")
    public ResponseEntity<String> generateTestUsers(@RequestParam(defaultValue = "100") int count) {
        List<User> users = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            User user = new User();
            user.setUsername("test" + i + "@example.com");
            user.setPassword(encoder.encode("password"));
            user.setName("Test User " + i);
            user.setLastname("Lastname " + i);
            user.setCity("City " + (i % 10));
            user.setAge(20 + (i % 30));
            user.setGender(i % 2 == 0 ? "Male" : "Female");
            user.setLanguages(Arrays.asList("English", "Russian"));
            user.setHobbies(Arrays.asList("Reading", "Sports"));
            user.setAboutme("About user " + i);
            user.setLookingFor("Looking for friends");
            user.setBioProvided(true);
            
            users.add(user);
        }
        
        int successCount = 0;
        for (User user : users) {
            try {
                if (!userRepository.existsByUsername(user.getUsername())) {
                    userRepository.save(user);
                    successCount++;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return ResponseEntity.ok("Generated " + successCount + " test users");
    }

    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        return ResponseEntity.ok("Deleted " + count + " users");
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("count", userRepository.count());
        return ResponseEntity.ok(response);
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
            response.put("languages", user.getLanguages());

            return response;
        }

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Unauthorized user - invalid or missing Bearer token");
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                    
            // Проверка прав доступа
            if (!canAccessUserProfile(user)) {
                throw new AccessDeniedException("Access denied");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("name", user.getName());
            response.put("image", user.getImage());
            return response;
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            // Оба исключения приведут к 404
            throw new ResourceNotFoundException("User not found");
        }
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

            // IMPORTANT DO NOT REMOVE IMPORTANT DO NOT REMOVE IMPORTANT DO NOT REMOVE
            // IMPORTANT DO NOT REMOVE
            if (image != null && !image.isEmpty() && !updates.containsKey("image")) {
                byte[] imageBytes = image.getBytes();
                updates.put("image", imageBytes);
            }

            // if (image != null && !image.isEmpty() && !updates.containsKey("image")) {
            // byte[] imageBytes = image.getBytes();
            // updates.put("image", imageBytes);
            // } else if (image == null) { // Обработка null для удаления изображения
            // updates.put("image", null);
            // }

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

    @GetMapping("/{id}/profile")
    public UserProfileDTO getUserProfileById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (!canAccessUserProfile(user)) {
                throw new AccessDeniedException("Access denied");
            }

            return userMapper.toProfileDTO(user);
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            throw new ResourceNotFoundException("User not found");
        }
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
        response.put("languages", user.getLanguages());

        return response;
    }

    private boolean canAccessUserProfile(User targetUser) {
        User currentUser = userProfileService.getCurrentUser();
        // Здесь можно добавить логику проверки доступа
        // Например, проверка на блокировку, приватность профиля и т.д.
        return true; // Пока возвращаем true
    }
}