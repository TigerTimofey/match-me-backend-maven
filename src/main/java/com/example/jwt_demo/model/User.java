package com.example.jwt_demo.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String name;

    private String lastname;

    private String city;

    private Integer age;

    private String gender;

    @ElementCollection
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    private List<String> hobbies = new ArrayList<>();

    private String image;

    private String aboutme;

    private String lookingFor;

    // Default Constructor
    public User() {
    }

    // Full Constructor
    public User(Long id, String username, String password, String name, String lastname, String city, Integer age, String gender, List<String> languages, List<String> hobbies, String image, String aboutme, String lookingFor) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.city = city;
        this.age = age;
        this.gender = gender;
        this.languages = languages;
        this.hobbies = hobbies;
        this.image = image;
        this.aboutme = aboutme;
        this.lookingFor = lookingFor;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public List<String> getHobbies() {
        return hobbies;
    }

    public void setHobbies(List<String> hobbies) {
        this.hobbies = hobbies;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public String getAboutme() {
        return aboutme;
    }
    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getLookingFor() { return lookingFor; }
    public void setLookingFor(String lookingFor) { this.lookingFor = lookingFor;}
}
