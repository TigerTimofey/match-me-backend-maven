package com.example.jwt_demo.mapper;

import org.springframework.stereotype.Component;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.model.User;

@Component
public class UserMapper {
    
    // Преобразование User в UserProfileDTO
    public UserProfileDTO toProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setCity(user.getCity());
        dto.setAge(user.getAge());
        dto.setGender(user.getGender());
        dto.setLanguages(user.getLanguages());
        dto.setHobbies(user.getHobbies());
        dto.setImage(user.getImage());
        dto.setAboutme(user.getAboutme());
        dto.setLookingFor(user.getLookingFor());
        return dto;
    }

    // Обновление существующего User из DTO
    public void updateUserFromDTO(User user, UserProfileDTO dto) {
        user.setName(dto.getName());
        user.setLastname(dto.getLastname());
        user.setCity(dto.getCity());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        user.setLanguages(dto.getLanguages());
        user.setHobbies(dto.getHobbies());
        user.setImage(dto.getImage());
        user.setAboutme(dto.getAboutme());
        user.setLookingFor(dto.getLookingFor());
    }
}
