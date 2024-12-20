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

    @Column(columnDefinition = "BYTEA")
    private byte[] image;
    private String aboutme;
    private String lookingFor;
    private Boolean isBioProvided = false;
    private Boolean bioProvided;

    public Boolean getBioProvided() {
        return bioProvided;
    }

    public void setBioProvided(Boolean bioProvided) {
        this.bioProvided = bioProvided;
    }
}
