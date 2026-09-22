package com.example.enterprise.common.core.context;

/**
 * Thread-local holder for the current login user.
 * Set by gateway/auth filter or Sa-Token integration; cleared after request.
 */
public final class UserContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();

    private UserContext() {
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    /**
     * @return current user id or throws if absent (for code paths that require login)
     */
    public static Long requireUserId() {
        Long id = USER_ID.get();
        if (id == null) {
            throw new IllegalStateException("UserContext is empty: user not authenticated");
        }
        return id;
    }

    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static void set(Long userId, String username) {
        setUserId(userId);
        setUsername(username);
    }

    /**
     * Must be called in finally / filter afterCompletion to avoid leaks.
     */
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
    }
}
