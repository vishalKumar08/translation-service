package com.digitaltolk.translationservice.service;

import com.digitaltolk.translationservice.api.dto.AuthRequest;
import com.digitaltolk.translationservice.api.dto.AuthResponse;
import com.digitaltolk.translationservice.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class AuthServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpiration", 86400000L);
        
        authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");
    }

    @Test
    void authenticate_AdminUser_Success() {
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getAccessToken());
        assertEquals("Bearer", result.getTokenType());
        assertEquals("admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        assertNotNull(result.getExpiresAt());
        assertNotNull(result.getIssuedAt());
        assertTrue(result.getExpiresAt().isAfter(LocalDateTime.now()));
        verify(jwtUtil).generateToken("admin", "ADMIN");
    }

    @Test
    void authenticate_EditorUser_Success() {
        authRequest.setUsername("editor");
        authRequest.setPassword("editor123");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getAccessToken());
        assertEquals("editor", result.getUsername());
        assertEquals("EDITOR", result.getRole());
        verify(jwtUtil).generateToken("editor", "EDITOR");
    }

    @Test
    void authenticate_ViewerUser_Success() {
        authRequest.setUsername("viewer");
        authRequest.setPassword("viewer123");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("mock-jwt-token", result.getAccessToken());
        assertEquals("viewer", result.getUsername());
        assertEquals("VIEWER", result.getRole());
        verify(jwtUtil).generateToken("viewer", "VIEWER");
    }

    @Test
    void authenticate_JohnAdminUser_Success() {
        authRequest.setUsername("john.admin");
        authRequest.setPassword("password123");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("john.admin", result.getUsername());
        assertEquals("ADMIN", result.getRole());
        verify(jwtUtil).generateToken("john.admin", "ADMIN");
    }

    @Test
    void authenticate_JaneEditorUser_Success() {
        authRequest.setUsername("jane.editor");
        authRequest.setPassword("password123");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("jane.editor", result.getUsername());
        assertEquals("EDITOR", result.getRole());
        verify(jwtUtil).generateToken("jane.editor", "EDITOR");
    }

    @Test
    void authenticate_BobViewerUser_Success() {
        authRequest.setUsername("bob.viewer");
        authRequest.setPassword("password123");
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");

        AuthResponse result = authService.authenticate(authRequest);

        assertNotNull(result);
        assertEquals("bob.viewer", result.getUsername());
        assertEquals("VIEWER", result.getRole());
        verify(jwtUtil).generateToken("bob.viewer", "VIEWER");
    }

    @Test
    void authenticate_InvalidUsername_ThrowsException() {
        authRequest.setUsername("nonexistent");
        authRequest.setPassword("admin123");

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsException() {
        authRequest.setUsername("admin");
        authRequest.setPassword("wrongpassword");

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_NullUsername_ThrowsException() {
        authRequest.setUsername(null);
        authRequest.setPassword("admin123");

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_EmptyUsername_ThrowsException() {
        authRequest.setUsername("");
        authRequest.setPassword("admin123");

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_NullPassword_ThrowsException() {
        authRequest.setUsername("admin");
        authRequest.setPassword(null);

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void authenticate_EmptyPassword_ThrowsException() {
        authRequest.setUsername("admin");
        authRequest.setPassword("");

        assertThrows(BadCredentialsException.class, () -> authService.authenticate(authRequest));
        verify(jwtUtil, never()).generateToken(anyString(), anyString());
    }

    @Test
    void getAvailableUsers_ReturnsAllUsers() {
        Map<String, String> users = authService.getAvailableUsers();

        assertNotNull(users);
        assertEquals(6, users.size());
        assertEquals("ADMIN", users.get("admin"));
        assertEquals("EDITOR", users.get("editor"));
        assertEquals("VIEWER", users.get("viewer"));
        assertEquals("ADMIN", users.get("john.admin"));
        assertEquals("EDITOR", users.get("jane.editor"));
        assertEquals("VIEWER", users.get("bob.viewer"));
    }

    @Test
    void getAvailableUsers_ContainsExpectedUsernames() {
        Map<String, String> users = authService.getAvailableUsers();

        assertTrue(users.containsKey("admin"));
        assertTrue(users.containsKey("editor"));
        assertTrue(users.containsKey("viewer"));
        assertTrue(users.containsKey("john.admin"));
        assertTrue(users.containsKey("jane.editor"));
        assertTrue(users.containsKey("bob.viewer"));
    }

    @Test
    void getAvailableUsers_ContainsExpectedRoles() {
        Map<String, String> users = authService.getAvailableUsers();

        assertTrue(users.containsValue("ADMIN"));
        assertTrue(users.containsValue("EDITOR"));
        assertTrue(users.containsValue("VIEWER"));
    }

    @Test
    void authenticate_ExpirationTimeCalculation() {
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");
        LocalDateTime beforeAuth = LocalDateTime.now();

        AuthResponse result = authService.authenticate(authRequest);

        LocalDateTime afterAuth = LocalDateTime.now();
        assertNotNull(result.getExpiresAt());
        assertTrue(result.getExpiresAt().isAfter(beforeAuth.plusHours(23)));
        assertTrue(result.getExpiresAt().isBefore(afterAuth.plusHours(25)));
    }

    @Test
    void authenticate_IssuedTimeSet() {
        when(jwtUtil.generateToken(anyString(), anyString())).thenReturn("mock-jwt-token");
        LocalDateTime beforeAuth = LocalDateTime.now();

        AuthResponse result = authService.authenticate(authRequest);

        LocalDateTime afterAuth = LocalDateTime.now();
        assertNotNull(result.getIssuedAt());
        assertTrue(result.getIssuedAt().isAfter(beforeAuth.minusSeconds(1)));
        assertTrue(result.getIssuedAt().isBefore(afterAuth.plusSeconds(1)));
    }
}
