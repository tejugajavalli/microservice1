package com.example.microservice_1.service;

import com.example.microservice_1.model.User;
import com.example.microservice_1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public boolean validateUser(String username, String rawPassword) {
        Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);
        if (userOptional.isEmpty()) return false;

        User user = userOptional.get();
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
