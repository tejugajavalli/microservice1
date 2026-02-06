package com.example.microservice_1.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/okta")
public class OktaSecuredController {



        @GetMapping("/profile")
        public ResponseEntity<String> getOktaProfile(@AuthenticationPrincipal OidcUser oidcUser) {
            String name = oidcUser.getFullName();
            String email = oidcUser.getEmail();
            return ResponseEntity.ok("Hello " + name + " (email: " + email + ")");
        }

        @GetMapping("/dashboard")
        public ResponseEntity<String> dashboard() {
            return ResponseEntity.ok("✅ You accessed this page using Okta Login");
        }
    }


