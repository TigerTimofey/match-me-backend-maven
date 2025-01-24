package com.example.jwt_demo.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
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

@Data
@Entity
@Table(name = "bios")
public class Bio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Boolean longWalks;
    private Boolean movies;

    @ElementCollection
    @CollectionTable(name = "bio_interests", joinColumns = @JoinColumn(name = "bio_id"))
    @Column(name = "interest")
    private List<String> interests;

    @Column(columnDefinition = "TEXT")
    private String about;
} 