package com.example.jwt_demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            return userRepository.findByUsername(username);
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
    }

    public UserProfileDTO getUserProfile(Long id) {
        User user = findUserById(id);
        return convertToProfileDTO(user);
    }

    @SuppressWarnings("unchecked")
    public User updateUser(Long id, Map<String, Object> updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Обновляем поля в зависимости от того, что передано в updates
        if (updates.containsKey("city")) {
            user.setCity((String) updates.get("city"));
        }
        if (updates.containsKey("age")) {
            user.setAge((Integer) updates.get("age"));
        }
        if (updates.containsKey("gender")) {
            user.setGender((String) updates.get("gender"));
        }
        if (updates.containsKey("image")) {
            user.setImage((byte[]) updates.get("image")); // Используем byte[] для обновления изображения
        }
        if (updates.containsKey("languages")) {
            user.setLanguages((List<String>) updates.get("languages"));
        }
        if (updates.containsKey("hobbies")) {
            user.setHobbies((List<String>) updates.get("hobbies"));
        }
        if (updates.containsKey("aboutme")) {
            user.setAboutme((String) updates.get("aboutme"));
        }
        if (updates.containsKey("lookingFor")) {
            user.setLookingFor((String) updates.get("lookingFor"));
        }
        if (updates.containsKey("bioProvided")) { // Add this check
            user.setBioProvided((Boolean) updates.get("bioProvided"));
        }

        // Сохраняем изменения в базе данных
        return userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();
        // Populate DTO with user data
        return dto;
    }

    @SuppressWarnings("unchecked")
    private void applyUpdates(User user, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            try {
                switch (key) {
                    case "username":
                        user.setUsername((String) value);
                        break;
                    case "password":
                        user.setPassword(encoder.encode((String) value));
                        break;
                    case "name":
                        user.setName((String) value);
                        break;
                    case "lastname":
                        user.setLastname((String) value);
                        break;
                    case "city":
                        user.setCity((String) value);
                        break;
                    case "age":
                        if (value instanceof Integer) {
                            user.setAge((Integer) value);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type for age field");
                        }
                        break;
                    case "gender":
                        if (value instanceof String) {
                            user.setGender((String) value);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type for gender field");
                        }
                        break;
                    case "languages":
                        if (value instanceof List) {
                            user.setLanguages((List<String>) value);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Invalid type for languages field");
                        }
                        break;
                    case "hobbies":
                        if (value instanceof List) {
                            user.setHobbies((List<String>) value);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type for hobbies field");
                        }
                        break;
                    case "image":
                        user.setImage((byte[]) value);
                        break;

                    case "aboutme":
                        user.setAboutme((String) value);
                        break;
                    case "lookingFor":
                        user.setLookingFor((String) value);
                        break;
                    case "isBioProvided":
                        if (value instanceof Boolean) {
                            user.setBioProvided((Boolean) value);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                    "Invalid type for isBioProvided field");
                        }
                        break;
                    default:
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid field: " + key);
                }
            } catch (ClassCastException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid type for " + key + " field");
            }
        });
    }

}
