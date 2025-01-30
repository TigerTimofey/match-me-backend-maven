package com.example.jwt_demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private String headline;
    private String location;
    private String education;
    private String work;
    private String aboutme;
    private String lookingFor;
    private String image;
}
