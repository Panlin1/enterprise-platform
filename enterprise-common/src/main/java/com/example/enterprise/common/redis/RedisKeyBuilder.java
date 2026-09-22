package com.example.enterprise.common.redis;

import com.example.enterprise.common.core.constant.CommonConstants;

/**
 * Unified Redis key convention:
 * <pre>
 * {system}:{module}:{business}:{identifier}
 * e.g. enterprise:auth:token:10001
 * </pre>
 */
public final class RedisKeyBuilder {

    private RedisKeyBuilder() {
    }

    public static String of(String module, String business, Object identifier) {
        return CommonConstants.REDIS_KEY_PREFIX + ":" + module + ":" + business + ":" + identifier;
    }

    public static String of(String module, String business) {
        return CommonConstants.REDIS_KEY_PREFIX + ":" + module + ":" + business;
    }

    // ---- Auth ----

    public static String authToken(Object userId) {
        return of("auth", "token", userId);
    }

    public static String authCaptcha(String uuid) {
        return of("auth", "captcha", uuid);
    }

    public static String authLoginFail(String username) {
        return of("auth", "login", "fail:" + username);
    }

    public static String rateLimitLogin(String username) {
        return of("rate_limit", "login", username);
    }

    // ---- User ----

    public static String userDetail(Object userId) {
        return of("user", "detail", userId);
    }

    public static String userPermissions(Object userId) {
        return of("user", "permissions", userId);
    }

    // ---- System ----

    public static String systemDict(String dictType) {
        return of("system", "dict", dictType);
    }
}
