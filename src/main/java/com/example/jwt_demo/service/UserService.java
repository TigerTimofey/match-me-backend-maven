package com.example.jwt_demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null && user.getBio() == null) {
            Bio bio = new Bio();
            bio.setUser(user);
            bio.setName(user.getName());
            bio.setLastname(user.getLastname());
            bio.setCity(user.getCity());
            bio.setAge(user.getAge());
            bio.setGender(user.getGender());
            bio.setLanguages(user.getLanguages());
            bio.setHobbies(user.getHobbies());
            bio.setAbout(user.getAboutme());
            user.setBio(bio);
            user = userRepository.save(user);
        }
        return user;
    }

    public Bio findBioById(Long id) {
        User user = findById(id);
        if (user != null && user.getBio() == null) {
            Bio bio = new Bio();
            bio.setUser(user);
            bio.setName(user.getName());
            bio.setLastname(user.getLastname());
            bio.setCity(user.getCity());
            bio.setAge(user.getAge());
            bio.setGender(user.getGender());
            bio.setLanguages(user.getLanguages());
            bio.setHobbies(user.getHobbies());
            bio.setAbout(user.getAboutme());
            user.setBio(bio);
            user = userRepository.save(user);
        }
        return user != null ? user.getBio() : null;
    }

    public Profile findProfileById(Long id) {
        User user = findById(id);
        return user != null ? user.getProfile() : null;
    }

    public List<User> getRecommendations(String username) {
        List<User> recommendations = new ArrayList<>();
        
        for (int i = 1; i <= 5; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("user" + i + "@example.com");
            user.setName("Test User " + i);
            user.setLastname("Lastname " + i);
            user.setAge(20 + i);
            user.setCity("City " + i);
            user.setGender(i % 2 == 0 ? "Male" : "Female");
            user.setHobbies(Arrays.asList("Hobby" + i, "Reading", "Travel"));
            user.setLanguages(Arrays.asList("English", "Language" + i));
            user.setAboutme("About user " + i);
            user.setLookingFor("Looking for friends");
            user.setBioProvided(true);
            
            // Создаем Bio для тестового пользователя
            Bio bio = new Bio();
            bio.setUser(user);
            bio.setName(user.getName());
            bio.setLastname(user.getLastname());
            bio.setCity(user.getCity());
            bio.setAge(user.getAge());
            bio.setGender(user.getGender());
            bio.setLanguages(user.getLanguages());
            bio.setHobbies(user.getHobbies());
            bio.setAbout(user.getAboutme());
            user.setBio(bio);
            
            recommendations.add(user);
        }
        
        return recommendations;
    }

    public List<User> getConnections(String username) {
        List<User> connections = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            User user = new User();
            user.setId((long) i);
            user.setUsername("connection" + i + "@example.com");
            user.setName("Connection " + i);
            user.setLastname("Friend " + i);
            user.setAge(25 + i);
            user.setCity("City " + i);
            user.setGender(i % 2 == 0 ? "Male" : "Female");
            user.setHobbies(Arrays.asList("Hobby" + i, "Music", "Sports"));
            user.setLanguages(Arrays.asList("English", "Spanish"));
            user.setAboutme("Connected user " + i);
            user.setLookingFor("Already connected!");
            user.setBioProvided(true);
            
            // Создаем Bio для тестового пользователя
            Bio bio = new Bio();
            bio.setUser(user);
            bio.setName(user.getName());
            bio.setLastname(user.getLastname());
            bio.setCity(user.getCity());
            bio.setAge(user.getAge());
            bio.setGender(user.getGender());
            bio.setLanguages(user.getLanguages());
            bio.setHobbies(user.getHobbies());
            bio.setAbout(user.getAboutme());
            user.setBio(bio);
            
            connections.add(user);
        }
        
        return connections;
    }
} 