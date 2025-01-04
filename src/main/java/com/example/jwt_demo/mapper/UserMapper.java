package com.example.jwt_demo.mapper;

import org.springframework.stereotype.Component;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.model.User;

@Component
public class UserMapper {
    
    // Преобразование User в UserProfileDTO
    public UserProfileDTO toProfileDTO(User user, boolean includePrivateInfo) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(user.getId());
        // Email включается только если includePrivateInfo = true
        if (includePrivateInfo) {
            dto.setUsername(user.getUsername());
        }
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
        dto.setBioProvided(user.getBioProvided() != null ? user.getBioProvided() : user.getIsBioProvided());
        return dto;
    }

    // Перегруженный метод для обратной совместимости
    public UserProfileDTO toProfileDTO(User user) {
        return toProfileDTO(user, false);
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
