package com.example.jwt_demo.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "bios")
public class Bio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String name;
    private String lastname;
    private String city;
    private Integer age;
    private String gender;

    @ElementCollection
    private List<String> languages = new ArrayList<>();

    @ElementCollection
    private List<String> hobbies = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String about;

    public Bio(Long id, User user, String name, String lastname, String city,
            Integer age, String gender, List<String> languages, List<String> hobbies, String about) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.lastname = lastname;
        this.city = city;
        this.age = age;
        this.gender = gender;
        this.languages = languages != null ? languages : new ArrayList<>();
        this.hobbies = hobbies != null ? hobbies : new ArrayList<>();
        this.about = about;
    }
}