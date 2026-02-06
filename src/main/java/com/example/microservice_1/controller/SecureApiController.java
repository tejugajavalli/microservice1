package com.example.microservice_1.controller;

import com.example.microservice_1.model.User;
import com.example.microservice_1.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")


public class SecureApiController {
    @Autowired
    UserRepository userRepository;


    @GetMapping("/secure")
    public ResponseEntity<String> getSecureData() {
        return ResponseEntity.ok("✅ You have accessed a protected API using a valid JWT!");
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMyDetails(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);

        return userOptional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/user-phone")
    public ResponseEntity<String> getUserphone() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);

        if (userOptional.isPresent()) {
            String phone = userOptional.get().getPhonenumber();
            return ResponseEntity.ok("user pahone number " + getUserphone());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user phone number not found");
        }
    }

    @PostMapping("/update-username")
    public ResponseEntity<String> updateUserName(User requestUser) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setFullname(requestUser.getFullname());
            return ResponseEntity.ok("user fullname updated succssfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("user fullname  not found");
        }
    }
}



