package com.example.enterprise.common.core.constant;


/**
 * Shared constants (non-error).
 */
public final class CommonConstants {

    private CommonConstants() {
    }

    /**
     * Soft-delete: normal
     */
    public static final int DELETED_NO = 0;

    /**
     * Soft-delete: deleted
     */
    public static final int DELETED_YES = 1;

    /**
     * User status: enabled
     */
    public static final int STATUS_ENABLED = 1;

    /**
     * User status: disabled
     */
    public static final int STATUS_DISABLED = 0;

    /**
     * Default Redis key prefix for this platform
     */
    public static final String REDIS_KEY_PREFIX = "enterprise";
}

