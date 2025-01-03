package com.example.jwt_demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("lastname", user.getLastname());
        response.put("city", user.getCity());
        response.put("age", user.getAge());
        response.put("genres", user.getGender());
        response.put("hobbies", user.getHobbies());
        response.put("languages", user.getLanguages());

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

        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Unauthorized user - invalid or missing Bearer token");
    }

    // CONNECTIONS
    // recommedations
    // @GetMapping("/recommendations")
    // public List<Long> getRecommendations() {
    // return userRepository.findAll()
    // .stream()
    // .map(User::getId)
    // .limit(11)
    // .toList();
    // }
    @GetMapping("/recommendations")
    public List<Long> getRecommendations() {
        List<Long> allUsers = userRepository.findAll()
                .stream()
                .map(User::getId)
                .collect(Collectors.toList());

        // Показываем только 10 пользователям
        return allUsers.stream().limit(100).toList();
    }

    // add dismissed
    @GetMapping("/{id}/dismissed")
    public Map<String, Object> getBasicUserDetails(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Prepare response map with id, name, and dismissed list
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("dismissed", user.getDismissed());

        return response;
    }

    // patch dismissed
    @PatchMapping("/{id}/dismissed")
    public ResponseEntity<User> updateDismissedUsers(
            @PathVariable Long id,
            @RequestBody List<Integer> newDismissed) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Update the 'dismissed' list with the new values
        user.setDismissed(newDismissed);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // add outcome requests
    @GetMapping("/{id}/outcome-requests")
    public Map<String, Object> getOutcomeReq(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Prepare response map with id, name, and dismissed list
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("outcomeRequests", user.getOutcomeRequests());

        return response;
    }

    // patch outcome requests
    @PatchMapping("/{id}/outcome-requests")
    public ResponseEntity<User> updateOutcomeReq(
            @PathVariable Long id,
            @RequestBody List<Integer> newOutcomeReq) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setOutcomeRequests(newOutcomeReq);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // add income requests
    @GetMapping("/{id}/income-requests")
    public Map<String, Object> getIncomeReq(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("incomeRequests", user.getIncomeRequests());

        return response;
    }

    // patch Income requests
    @PatchMapping("/{id}/income-requests")
    public ResponseEntity<User> updateIncomeReq(
            @PathVariable Long id,
            @RequestBody List<Integer> newIncomeReq) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setIncomeRequests(newIncomeReq);
        userRepository.save(user);

        return ResponseEntity.ok(user);
    }

    // add connections requests
    @GetMapping("/{id}/connections")
    public Map<String, Object> getConnectionsReq(@PathVariable Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("connections", user.getConnections());

        return response;
    }

    // patch connections requests
    @PatchMapping("/{id}/connections")
    public ResponseEntity<User> updateConnectionsReq(
            @PathVariable Long id,
            @RequestBody List<Integer> newConnectionsReq) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setConnections(newConnectionsReq);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }
}