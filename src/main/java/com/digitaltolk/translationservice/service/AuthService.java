package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.AuthRequest;
import com.digitaltolk.translationservice.api.dto.AuthResponse;
import com.digitaltolk.translationservice.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final JwtUtil jwtUtil;
    private final Map<String, UserCredentials> users;

    @Value("${spring.security.jwt.expiration}")
    private Long jwtExpiration;

    public AuthService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.users = initializeUsers();
    }

    public AuthResponse authenticate(AuthRequest authRequest) {
        logger.info("Authentication attempt for user: {}", authRequest.getUsername());

        UserCredentials user = users.get(authRequest.getUsername());
        if (user == null || !user.password.equals(authRequest.getPassword())) {
            logger.warn("Authentication failed for user: {}", authRequest.getUsername());
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(authRequest.getUsername(), user.role);
        
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(jwtExpiration / 1000);

        logger.info("Authentication successful for user: {} with role: {}", authRequest.getUsername(), user.role);

        return new AuthResponse(token, authRequest.getUsername(), user.role, expiresAt);
    }

    private Map<String, UserCredentials> initializeUsers() {
        Map<String, UserCredentials> userMap = new HashMap<>();
        //test users
        userMap.put("admin", new UserCredentials("admin123", "ADMIN"));
        userMap.put("editor", new UserCredentials("editor123", "EDITOR"));
        userMap.put("viewer", new UserCredentials("viewer123", "VIEWER"));
        
        userMap.put("john.admin", new UserCredentials("password123", "ADMIN"));
        userMap.put("jane.editor", new UserCredentials("password123", "EDITOR"));
        userMap.put("bob.viewer", new UserCredentials("password123", "VIEWER"));
        
        return userMap;
    }

    public Map<String, String> getAvailableUsers() {
        Map<String, String> availableUsers = new HashMap<>();
        users.forEach((username, credentials) -> 
            availableUsers.put(username, credentials.role));
        return availableUsers;
    }

    private static class UserCredentials {
        final String password;
        final String role;

        UserCredentials(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }
}
