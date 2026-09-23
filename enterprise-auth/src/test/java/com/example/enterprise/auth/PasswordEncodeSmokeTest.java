package com.example.enterprise.auth;


import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Ensures seed password hash in 02_data.sql matches BCrypt of "123456".
 */
class PasswordEncodeSmokeTest {

    private static final String SEED_HASH =
            "$2b$10$2KwTr1/bkPPQnpykXHEGu.33s0xjgS5AU.EpoZW.A.zBZUeAUHsQO";

    @Test
    void seedPasswordMatches123456() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        assertTrue(encoder.matches("123456", SEED_HASH));
    }
}

