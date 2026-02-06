package com.example.microservice_1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.microservice_1.model.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class OktaService {

    @Value("${okta.api.token}")
    private String oktaApiToken;

    @Value("${okta.api.url}")
    private String oktaBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Map<String, String> ROLE_TO_GROUP_ID = new HashMap<>();

    static {
        ROLE_TO_GROUP_ID.put("Admin", "your-okta-admin-group-id");
        ROLE_TO_GROUP_ID.put("User", "your-okta-user-group-id");
        ROLE_TO_GROUP_ID.put("Manager", "your-okta-manager-group-id");
    }

    public void createUserInOkta(User user) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("firstName", user.getFirstname());
        profile.put("lastName", user.getLastname());
        profile.put("email", user.getUsername());
        profile.put("login", user.getUsername());
        profile.put("externalId", user.getId());

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("password", Map.of("value", user.getPassword()));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("profile", profile);
        requestBody.put("credentials", credentials);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(oktaApiToken.replace("SSWS ", ""));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                oktaBaseUrl + "/api/v1/users?activate=true", entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
            String oktaUserId = (String) ((Map<?, ?>) response.getBody()).get("id");
            assignGroup(oktaUserId, user.getRole());
        } else {
            throw new RuntimeException("Failed to create user in Okta: " + response.getBody());
        }
    }

    private void assignGroup(String oktaUserId, String role) {
        String groupId = ROLE_TO_GROUP_ID.get(role);
        if (groupId == null) {
            throw new IllegalArgumentException("Invalid role: " + role);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(oktaApiToken.replace("SSWS ", ""));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.put(
                oktaBaseUrl + "/api/v1/groups/" + groupId + "/users/" + oktaUserId,
                entity);
    }
}
