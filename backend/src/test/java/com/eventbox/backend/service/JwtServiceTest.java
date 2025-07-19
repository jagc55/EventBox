package com.eventbox.backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    private final String testSecretKey = "mySecretKeyForTestingPurposesItShouldBeLongEnoughForHS256Algorithm";
    private final String testUsername = "testuser@example.com";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", testSecretKey);
    }

    @Test
    void generateToken_ShouldReturnValidJwtToken() {
        // When
        String token = jwtService.generateToken(testUsername);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
        
        // Verify token structure (should have 3 parts separated by dots)
        String[] tokenParts = token.split("\\.");
        assertEquals(3, tokenParts.length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsername() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertEquals(testUsername, extractedUsername);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaimValue() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        String subject = jwtService.extractClaim(token, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(token, Claims::getIssuedAt);
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);

        // Then
        assertEquals(testUsername, subject);
        assertNotNull(issuedAt);
        assertNotNull(expiration);
        assertTrue(expiration.after(issuedAt));
        
        // Check that token expires in approximately 24 hours
        long expectedExpirationTime = issuedAt.getTime() + (1000 * 60 * 60 * 24);
        long actualExpirationTime = expiration.getTime();
        long timeDifference = Math.abs(expectedExpirationTime - actualExpirationTime);
        assertTrue(timeDifference < 1000); // Allow 1 second difference
    }

    @Test
    void isTokenValid_WithValidTokenAndMatchingUsername_ShouldReturnTrue() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        boolean isValid = jwtService.isTokenValid(token, testUsername);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_WithValidTokenButDifferentUsername_ShouldReturnFalse() {
        // Given
        String token = jwtService.generateToken(testUsername);
        String differentUsername = "different@example.com";

        // When
        boolean isValid = jwtService.isTokenValid(token, differentUsername);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isTokenValid_WithExpiredToken_ShouldReturnFalse() {
        Key signingKey = Keys.hmacShaKeyFor(testSecretKey.getBytes());
        String expiredToken = Jwts.builder()
                .setSubject(testUsername)
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000)) 
                .signWith(signingKey)
                .compact();

        try {
            boolean isValid = jwtService.isTokenValid(expiredToken, testUsername);
            assertFalse(isValid);
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("expired") || e instanceof io.jsonwebtoken.ExpiredJwtException);
        }
    }

    @Test
    void extractUsername_WithInvalidToken_ShouldThrowException() {
        // Given
        String invalidToken = "invalid.token.structure";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.extractUsername(invalidToken));
    }

    @Test
    void extractUsername_WithMalformedToken_ShouldThrowException() {
        // Given
        String malformedToken = "eyJhbGciOiJIUzI1NiJ9.invalid_payload.signature";

        // When & Then
        assertThrows(Exception.class, () -> jwtService.extractUsername(malformedToken));
    }

    @Test
    void generateToken_WithNullUsername_ShouldHandleGracefully() {
        // When & Then
        assertDoesNotThrow(() -> {
            String token = jwtService.generateToken(null);
            assertNotNull(token);
        });
    }

    @Test
    void generateToken_WithEmptyUsername_ShouldCreateTokenWithEmptySubject() {
        // Given
        String emptyUsername = "";

        // When
        String token = jwtService.generateToken(emptyUsername);
        String extractedUsername = jwtService.extractUsername(token);

        // Then
        assertNotNull(token);
        // Note: If the implementation stores null instead of empty string, adjust expectation
        assertTrue(extractedUsername == null || extractedUsername.equals(emptyUsername));
    }

    @Test
    void extractClaim_ShouldHandleTokenExpirationCorrectly() {
        // Given
        String token = jwtService.generateToken(testUsername);

        // When
        Date expiration = jwtService.extractClaim(token, Claims::getExpiration);
        Date currentTime = new Date();

        // Then
        assertTrue(expiration.after(currentTime), "Token should not be expired immediately after creation");
    }
}
