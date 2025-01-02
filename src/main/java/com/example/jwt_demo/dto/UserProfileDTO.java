package com.example.jwt_demo.dto;

import java.util.List;

import lombok.Data;

@Data
public class UserProfileDTO {
    private Long id;
    private String username;
    private String name;
    private String lastname;
    private String city;
    private Integer age;
    private String gender;
    private List<String> languages;
    private List<String> hobbies;
    private byte[] image;
    private String aboutme;
    private String lookingFor;
}
