package com.example.jwt_demo.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.jwt_demo.dto.UserProfileDTO;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {
    private final UserRepository userRepository;

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
            user.setImage((byte[]) updates.get("image"));
        }
        if (updates.containsKey("languages")) {
            user.setLanguages((List<String>) updates.get("languages"));
        }
        if (updates.containsKey("hobbies")) {
            user.setHobbies((List<String>) updates.get("hobbies"));
        }
        if (updates.containsKey("dismissed")) {
            user.setDismissed((List<Integer>) updates.get("dismissed"));
        }
        if (updates.containsKey("outcomeRequests")) {
            user.setOutcomeRequests((List<Integer>) updates.get("outcomeRequests"));
        }
        if (updates.containsKey("incomeRequests")) {
            user.setIncomeRequests((List<Integer>) updates.get("incomeRequests"));
        }
        if (updates.containsKey("aboutme")) {
            user.setAboutme((String) updates.get("aboutme"));
        }
        if (updates.containsKey("lookingFor")) {
            user.setLookingFor((String) updates.get("lookingFor"));
        }
        if (updates.containsKey("bioProvided")) {
            user.setBioProvided((Boolean) updates.get("bioProvided"));
        }

        return userRepository.save(user);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserProfileDTO convertToProfileDTO(User user) {
        UserProfileDTO dto = new UserProfileDTO();

        return dto;
    }

}
