package com.example.enterprise.common.redis;

import java.time.Duration;

/**
 * Redis TTL and cache convention constants.
 */
public final class RedisConstants {

    private RedisConstants() {
    }

    /**
     * Captcha TTL
     */
    public static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);

    /**
     * Login failure counter TTL (lock window)
     */
    public static final Duration LOGIN_FAIL_TTL = Duration.ofMinutes(15);

    /**
     * Access token session TTL (example; Sa-Token may manage its own)
     */
    public static final Duration TOKEN_TTL = Duration.ofHours(2);

    /**
     * User detail cache TTL
     */
    public static final Duration USER_DETAIL_TTL = Duration.ofMinutes(30);

    /**
     * User permission set cache TTL
     */
    public static final Duration USER_PERMISSIONS_TTL = Duration.ofMinutes(30);

    /**
     * Dict cache TTL
     */
    public static final Duration DICT_TTL = Duration.ofHours(1);

    /**
     * Empty-value cache TTL (anti cache penetration)
     */
    public static final Duration NULL_VALUE_TTL = Duration.ofMinutes(2);

    /**
     * Marker stored for null / miss to prevent penetration
     */
    public static final String NULL_PLACEHOLDER = "__NULL__";

    /**
     * Random jitter upper bound (seconds) to avoid cache avalanche
     */
    public static final long TTL_JITTER_SECONDS = 60L;
}
