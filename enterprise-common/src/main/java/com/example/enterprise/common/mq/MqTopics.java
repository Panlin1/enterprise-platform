package com.example.enterprise.common.mq;

/**
 * RocketMQ topic / tag conventions.
 * <pre>
 *   Topic: enterprise-{domain}
 *   Tag:   event type
 * </pre>
 */
public final class MqTopics {

    private MqTopics() {
    }

    /**
     * 登录日志异步落库
     */
    public static final String TOPIC_LOGIN_LOG = "enterprise-login-log";

    /**
     * 操作日志异步落库
     */
    public static final String TOPIC_OPERATION_LOG = "enterprise-operation-log";

    public static final String TAG_LOGIN_SUCCESS = "LOGIN_SUCCESS";
    public static final String TAG_LOGIN_FAIL = "LOGIN_FAIL";
    public static final String TAG_LOGOUT = "LOGOUT";

    public static final String TAG_OPERATION = "OPERATION";
}

