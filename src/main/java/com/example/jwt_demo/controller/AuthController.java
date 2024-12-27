package com.example.jwt_demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;
import com.example.jwt_demo.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    private JwtUtil jwtUtils;

    @PostMapping("/signin")
    public String authenticateUser(@RequestBody User user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        user.getPassword()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return jwtUtils.generateToken(userDetails.getUsername());
    }

    @PostMapping("/signup")
    public String registerUser(@RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return "Error: Email is already taken!";
        }
        User newUser = new User(
                null,
                user.getUsername(),
                encoder.encode(user.getPassword()),
                user.getName(),
                user.getLastname(),
                user.getCity(),
                user.getAge(),
                user.getGender(),
                user.getLanguages(),
                user.getHobbies(),
                user.getImage(),
                user.getAboutme(),
                user.getLookingFor(),
                user.getBioProvided(), null);
        userRepository.save(newUser);
        return "User registered successfully!";
    }

    @PostMapping("/signup/batch")
    public ResponseEntity<String> registerBatchUsers(@RequestBody List<User> users) {
        int successCount = 0;
        for (User user : users) {
            try {
                if (!userRepository.existsByUsername(user.getUsername())) {
                    User newUser = new User(
                        null,
                        user.getUsername(),
                        encoder.encode(user.getPassword()),
                        user.getName(),
                        user.getLastname(),
                        user.getCity(),
                        user.getAge(),
                        user.getGender(),
                        user.getLanguages(),
                        user.getHobbies(),
                        user.getImage(),
                        user.getAboutme(),
                        user.getLookingFor(),
                        user.getBioProvided(),
                        null
                    );
                    userRepository.save(newUser);
                    successCount++;
                }
            } catch (Exception e) {
                // Пропускаем неудачные попытки
                continue;
            }
        }
        return ResponseEntity.ok("Successfully registered " + successCount + " users out of " + users.size());
    }

    @GetMapping("/me")
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            User user = userRepository.findByUsername(username);
            if (user == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
            }
            return user;
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
    }
}
