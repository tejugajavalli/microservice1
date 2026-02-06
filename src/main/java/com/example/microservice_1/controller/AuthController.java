package com.example.microservice_1.controller;

import com.example.microservice_1.Util.JwtUtil;
import com.example.microservice_1.dto.LoginRequest;
import com.example.microservice_1.model.User;
import com.example.microservice_1.repository.UserRepository;
import com.example.microservice_1.service.OktaService;
import com.example.microservice_1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.example.microservice_1.service.AuthService;

import java.util.Optional;

@RestController
@RequestMapping("/auth")

public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private OktaService oktaService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        userService.registerUser(user);
        oktaService.createUserInOkta(user);
        return ResponseEntity.ok("User registered in DB and Okta successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean valid = authService.validateUser(loginRequest.getUsername(), loginRequest.getPassword());

        if (!valid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }

        // Generate JWT token after successful login
        String token = jwtUtil.generateToken(loginRequest.getUsername());
        System.out.println("Login success. Token: " + token);
        return  ResponseEntity.ok("Bearer " + token);

    }

}
