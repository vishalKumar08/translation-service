package com.digitaltolk.translationservice.api.controller;

import com.digitaltolk.translationservice.api.dto.AuthRequest;
import com.digitaltolk.translationservice.api.dto.AuthResponse;
import com.digitaltolk.translationservice.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for JWT token generation")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
        security = @SecurityRequirement(name = ""),
        summary = "Authenticate user and generate JWT token",
        description = "Authenticates a user with username and password, returns a JWT token for API access"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Authentication successful",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class),
                examples = @ExampleObject(
                    name = "Successful Authentication",
                    value = """
                    {
                        "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTcwNDEwMDgwMCwiZXhwIjoxNzA0MTg3MjAwfQ...",
                        "tokenType": "Bearer",
                        "username": "admin",
                        "role": "ADMIN",
                        "expiresAt": "2024-01-02T10:00:00",
                        "issuedAt": "2024-01-01T10:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Authentication failed - Invalid credentials",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Authentication Failed",
                    value = """
                    {
                        "status": 401,
                        "error": "Unauthorized",
                        "message": "Invalid username or password",
                        "path": "/api/v1/auth/login",
                        "timestamp": "2024-01-01T10:00:00"
                    }
                    """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Bad request - Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    value = """
                    {
                        "status": 400,
                        "error": "Validation Failed",
                        "message": "Input validation failed",
                        "fieldErrors": {
                            "username": "Username is required",
                            "password": "Password must be between 6 and 100 characters"
                        }
                    }
                    """
                )
            )
        )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody 
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "User credentials for authentication",
                required = true,
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = AuthRequest.class),
                    examples = {
                        @ExampleObject(
                            name = "Admin Login",
                            description = "Login as admin user",
                            value = """
                            {
                                "username": "admin",
                                "password": "admin123"
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Editor Login",
                            description = "Login as editor user",
                            value = """
                            {
                                "username": "editor",
                                "password": "editor123"
                            }
                            """
                        ),
                        @ExampleObject(
                            name = "Viewer Login",
                            description = "Login as viewer user",
                            value = """
                            {
                                "username": "viewer",
                                "password": "viewer123"
                            }
                            """
                        )
                    }
                )
            ) AuthRequest authRequest) {
        
        logger.info("Login request received for user: {}", authRequest.getUsername());
        
        try {
            AuthResponse authResponse = authService.authenticate(authRequest);
            logger.info("Login successful for user: {}", authRequest.getUsername());
            return ResponseEntity.ok(authResponse);
        } catch (BadCredentialsException e) {
            logger.warn("Login failed for user: {} - {}", authRequest.getUsername(), e.getMessage());
            throw e; // Will be handled by GlobalExceptionHandler
        }
    }

    @Operation(
            summary = "Get available demo users",
            description = "Returns a list of available demo users for testing purposes"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "List of available demo users",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Available Users",
                    value = """
                    {
                        "admin": "ADMIN",
                        "editor": "EDITOR",
                        "viewer": "VIEWER",
                        "john.admin": "ADMIN",
                        "jane.editor": "EDITOR",
                        "bob.viewer": "VIEWER"
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/users")
    public ResponseEntity<Map<String, String>> getAvailableUsers() {
        logger.debug("Fetching available demo users");
        Map<String, String> users = authService.getAvailableUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(
        summary = "Get authentication information",
        description = "Returns information about the authentication system and available endpoints"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Authentication system information",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Auth Info",
                    value = """
                    {
                        "system": "JWT Authentication",
                        "tokenType": "Bearer",
                        "loginEndpoint": "/auth/login",
                        "usersEndpoint": "/auth/users",
                        "description": "Use POST /auth/login with username and password to get a JWT token. Include the token in the Authorization header as 'Bearer <token>' for authenticated requests.",
                        "roles": ["ADMIN", "EDITOR", "VIEWER"],
                        "permissions": {
                            "ADMIN": "Full access - can create, read, update, delete all resources",
                            "EDITOR": "Can create, read, update translations and tags",
                            "VIEWER": "Read-only access to translations and tags"
                        }
                    }
                    """
                )
            )
        )
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getAuthInfo() {
        logger.debug("Fetching authentication system information");
        
        Map<String, Object> authInfo = Map.of(
            "system", "JWT Authentication",
            "tokenType", "Bearer",
            "loginEndpoint", "/auth/login",
            "usersEndpoint", "/auth/users",
            "description", "Use POST /auth/login with username and password to get a JWT token. Include the token in the Authorization header as 'Bearer <token>' for authenticated requests.",
            "roles", new String[]{"ADMIN", "EDITOR", "VIEWER"},
            "permissions", Map.of(
                "ADMIN", "Full access - can create, read, update, delete all resources",
                "EDITOR", "Can create, read, update translations and tags",
                "VIEWER", "Read-only access to translations and tags"
            )
        );
        
        return ResponseEntity.ok(authInfo);
    }
}
