package com.vedant.jobtracker.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

// A pure unit test - no Spring context, no database. @Value fields are
// normally injected by Spring; since there's no Spring context here,
// ReflectionTestUtils sets them directly instead.
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "jwtSecret",
                "TestSecretKeyForJWTSigningInTestsOnly1234567890");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 86400000L);
    }

    @Test
    void generateToken_thenExtractEmail_returnsOriginalEmail() {
        String token = jwtService.generateToken("test@example.com");

        assertEquals("test@example.com", jwtService.extractEmail(token));
    }

    @Test
    void isTokenValid_withFreshToken_returnsTrue() {
        String token = jwtService.generateToken("test@example.com");

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_withTamperedToken_returnsFalse() {
        String token = jwtService.generateToken("test@example.com");
        // Corrupt the signature portion at the end of the token.
        String tampered = token.substring(0, token.length() - 5) + "abcde";

        assertFalse(jwtService.isTokenValid(tampered));
    }

    @Test
    void isTokenValid_withExpiredToken_returnsFalse() {
        // Negative expiration means the token is already expired the
        // instant it's created.
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", -1000L);
        String token = jwtService.generateToken("test@example.com");

        assertFalse(jwtService.isTokenValid(token));
    }
}
