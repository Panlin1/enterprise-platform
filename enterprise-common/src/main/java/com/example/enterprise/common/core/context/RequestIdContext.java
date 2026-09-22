package com.example.enterprise.common.core.context;

/**
 * Thread-local request id (trace correlation for logs).
 * Header name convention: {@code X-Request-Id}.
 */
public final class RequestIdContext {

    public static final String HEADER_NAME = "X-Request-Id";

    private static final ThreadLocal<String> REQUEST_ID = new ThreadLocal<>();

    private RequestIdContext() {
    }

    public static void set(String requestId) {
        REQUEST_ID.set(requestId);
    }

    public static String get() {
        return REQUEST_ID.get();
    }

    public static void clear() {
        REQUEST_ID.remove();
    }
}
