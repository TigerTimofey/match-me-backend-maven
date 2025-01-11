package com.example.jwt_demo.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @ElementCollection
    private List<Integer> dismissed = new ArrayList<>();

    @ElementCollection
    private List<Integer> outcomeRequests = new ArrayList<>();

    @ElementCollection
    private List<Integer> incomeRequests = new ArrayList<>();

    @ElementCollection
    private List<Integer> connections = new ArrayList<>();

    @Column(columnDefinition = "BYTEA")
    private byte[] image;
    private String aboutme;
    private String lookingFor;
    private Boolean isBioProvided = false;
    private Boolean bioProvided = false;

    public User(Long id, String username, String password, String name, String lastname,
                String city, Integer age, String gender, List<String> languages,
                List<String> hobbies, byte[] image, String aboutme, String lookingFor,
                Boolean bioProvided, Boolean isBioProvided) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastname = lastname;
        this.city = city;
        this.age = age;
        this.gender = gender;
        this.languages = languages != null ? languages : new ArrayList<>();
        this.hobbies = hobbies != null ? hobbies : new ArrayList<>();
        this.image = image;
        this.aboutme = aboutme;
        this.lookingFor = lookingFor;
        this.bioProvided = bioProvided;
        this.isBioProvided = isBioProvided;
        this.dismissed = new ArrayList<>();
        this.outcomeRequests = new ArrayList<>();
        this.incomeRequests = new ArrayList<>();
        this.connections = new ArrayList<>();
    }

    public Boolean getBioProvided() {
        return bioProvided != null ? bioProvided : isBioProvided;
    }

    public void setBioProvided(Boolean bioProvided) {
        this.bioProvided = bioProvided;
        this.isBioProvided = bioProvided;
    }
}
