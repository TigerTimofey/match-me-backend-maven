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
import org.springframework.web.client.RestTemplate;
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
    private final RestTemplate restTemplate;

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<UserProfileDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserProfileDTO> userDTOs = users.stream()
                .map(userMapper::toProfileDTO)
                .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Generate test users with avatars
    @GetMapping("/generate")
    public ResponseEntity<String> generateTestUsers(@RequestParam(defaultValue = "100") int count) {
        List<User> users = new ArrayList<>();
        List<String> cities = Arrays.asList("Tallinn", "Tartu", "Narva", "Loksa");
        
        for (int i = 0; i < count; i++) {
            try {
                User user = new User();
                String email = "test" + i + "@example.com";
                
                if (userRepository.existsByUsername(email)) {
                    System.out.println("User with email " + email + " already exists, skipping...");
                    continue;
                }
                
                user.setUsername(email);
                user.setPassword(encoder.encode("password"));
                user.setName("Test User " + i);
                user.setLastname("Lastname " + i);
                user.setCity(cities.get(i % cities.size()));
                user.setAge(20 + (i % 30));
                user.setGender(i % 2 == 0 ? "Male" : "Female");
                user.setLanguages(Arrays.asList("English", "Russian"));
                user.setHobbies(Arrays.asList("Reading", "Sports"));
                user.setAboutme("About user " + i);
                user.setLookingFor("Looking for friends");
                user.setBioProvided(true);
                
                // Avatar generation
                try {
                    String avatarUrl = "https://api.dicebear.com/7.x/avataaars/png?seed=" + email;
                    byte[] imageBytes = restTemplate.getForObject(avatarUrl, byte[].class);
                    if (imageBytes != null && imageBytes.length > 0) {
                        user.setImage(imageBytes);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to generate avatar for user " + email + ": " + e.getMessage());
                }
                
                userRepository.save(user);
                users.add(user);
                
            } catch (Exception e) {
                System.err.println("Error processing user: " + e.getMessage());
            }
        }
        
        return ResponseEntity.ok("Generated " + users.size() + " test users with avatars");
    }

    // Delete all users
    @DeleteMapping("/all")
    public ResponseEntity<String> deleteAllUsers() {
        long count = userRepository.count();
        userRepository.deleteAll();
        return ResponseEntity.ok("Deleted " + count + " users");
    }

    // Get total user count
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        Map<String, Long> response = new HashMap<>();
        response.put("count", userRepository.count());
        return ResponseEntity.ok(response);
    }

    // Get current user profile
    @GetMapping("/me")
    public UserProfileDTO getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username);
            return userMapper.toProfileDTO(user, true); // Включаем приватную информацию
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }

    // Get current user's detailed profile
    @GetMapping("/me/profile")
    public UserProfileDTO getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username);
            return userMapper.toProfileDTO(user, true); // Включаем приватную информацию
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    // Get current user's bio information
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
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("lastname", user.getLastname());
            response.put("city", user.getCity());
            response.put("age", user.getAge());
            response.put("gender", user.getGender());
            response.put("hobbies", user.getHobbies());
            response.put("languages", user.getLanguages());

            return response;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    // Get user by ID
    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            
            if (!canAccessUserProfile(user)) {
                throw new AccessDeniedException("Access denied");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("name", user.getName());
            response.put("image", user.getImage());
            return response;
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    // Delete user by ID
    @DeleteMapping("/{id}")
    public String deleteUserById(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "User deleted successfully!";
        }
        return "Error: User not found!";
    }

    // Update user by ID
    @PatchMapping("/{id}")
    public User updateUserById(
            @PathVariable Long id,
            @RequestParam("data") String userBioData,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> updates = objectMapper.readValue(userBioData, 
                    new TypeReference<Map<String, Object>>() {});

            if (image != null && !image.isEmpty()) {
                updates.put("image", image.getBytes());
            }

            return userProfileService.updateUser(id, updates);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid input data", e);
        }
    }

    // Get user profile by ID
    @GetMapping("/{id}/profile")
    public UserProfileDTO getUserProfileById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (!canAccessUserProfile(user)) {
                throw new AccessDeniedException("Access denied");
            }

            User currentUser = userProfileService.getCurrentUser();
            boolean isOwner = currentUser != null && currentUser.getId().equals(user.getId());

            return userMapper.toProfileDTO(user, isOwner);
        } catch (ResourceNotFoundException | AccessDeniedException e) {
            throw new ResourceNotFoundException("User not found");
        }
    }

    // Get user bio by ID
    @GetMapping("/{id}/bio")
    public Map<String, Object> getUserBioById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("lastname", user.getLastname());
        response.put("city", user.getCity());
        response.put("age", user.getAge());
        response.put("gender", user.getGender());
        response.put("hobbies", user.getHobbies());
        response.put("languages", user.getLanguages());

        return response;
    }

    // Get user recommendations
    @GetMapping("/recommendations")
    public List<Long> getRecommendations() {
        return userRepository.findAll()
                .stream()
                .map(User::getId)
                .limit(11)
                .toList();
    }

    // Helper method to check if current user can access target user's profile
    private boolean canAccessUserProfile(User targetUser) {
        User currentUser = userProfileService.getCurrentUser();
        return true; // Пока возвращаем true
    }
}