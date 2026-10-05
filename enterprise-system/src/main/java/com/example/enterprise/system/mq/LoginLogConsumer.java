package com.example.enterprise.system.mq;

import com.example.enterprise.common.mq.LoginLogMessage;
import com.example.enterprise.common.mq.MqTopics;
import com.example.enterprise.system.entity.SysLoginLog;
import com.example.enterprise.system.mapper.SysLoginLogMapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * Consume login events and persist to sys_login_log.
 */
@Service
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true", matchIfMissing = false)
@RocketMQMessageListener(
        topic = MqTopics.TOPIC_LOGIN_LOG,
        consumerGroup = "enterprise-system-login-log-group"
)
public class LoginLogConsumer implements RocketMQListener<LoginLogMessage> {

    private static final Logger log = LoggerFactory.getLogger(LoginLogConsumer.class);

    private final SysLoginLogMapper loginLogMapper;

    public LoginLogConsumer(SysLoginLogMapper loginLogMapper) {
        this.loginLogMapper = loginLogMapper;
    }

    @Override
    public void onMessage(LoginLogMessage message) {
        if (message == null) {
            return;
        }
        try {
            SysLoginLog entity = new SysLoginLog();
            entity.setUserId(message.getUserId());
            entity.setUsername(message.getUsername());
            entity.setLoginType(1);
            entity.setIp(message.getIp());
            entity.setUserAgent(message.getUserAgent());
            entity.setMessage(message.getMessage());
            entity.setLoginAt(message.getEventTime());
            int status = MqTopics.TAG_LOGIN_SUCCESS.equals(message.getEventType())
                    || MqTopics.TAG_LOGOUT.equals(message.getEventType()) ? 1 : 0;
            entity.setStatus(status);
            loginLogMapper.insert(entity);
            log.info("Login log saved username={} type={}", message.getUsername(), message.getEventType());
        } catch (Exception e) {
            log.error("Persist login log failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
