package com.example.enterprise.auth.mq;

import com.example.enterprise.common.mq.LoginLogMessage;
import com.example.enterprise.common.mq.MqTopics;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Publish login events to RocketMQ (optional when rocketmq.enabled=true).
 */
@Component
@ConditionalOnProperty(name = "rocketmq.enabled", havingValue = "true", matchIfMissing = false)
public class LoginLogProducer {

    private static final Logger log = LoggerFactory.getLogger(LoginLogProducer.class);

    private final RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic-login-log:" + MqTopics.TOPIC_LOGIN_LOG + "}")
    private String topic;

    @Autowired
    public LoginLogProducer(RocketMQTemplate rocketMQTemplate) {
        this.rocketMQTemplate = rocketMQTemplate;
    }

    public void sendSuccess(Long userId, String username, String ip, String userAgent) {
        send(userId, username, MqTopics.TAG_LOGIN_SUCCESS, ip, userAgent, "登录成功");
    }

    public void sendFail(String username, String ip, String userAgent, String reason) {
        send(null, username, MqTopics.TAG_LOGIN_FAIL, ip, userAgent, reason);
    }

    public void sendLogout(Long userId, String username, String ip) {
        send(userId, username, MqTopics.TAG_LOGOUT, ip, null, "退出登录");
    }

    private void send(Long userId, String username, String tag, String ip, String ua, String message) {
        try {
            LoginLogMessage payload = new LoginLogMessage();
            payload.setUserId(userId);
            payload.setUsername(username);
            payload.setEventType(tag);
            payload.setIp(ip);
            payload.setUserAgent(ua);
            payload.setMessage(message);
            payload.setEventTime(LocalDateTime.now());
            String destination = topic + ":" + tag;
            rocketMQTemplate.asyncSend(destination, MessageBuilder.withPayload(payload).build(),
                    new org.apache.rocketmq.client.producer.SendCallback() {
                        @Override
                        public void onSuccess(org.apache.rocketmq.client.producer.SendResult sendResult) {
                            log.debug("Login log sent topic={} tag={} msgId={}",
                                    topic, tag, sendResult.getMsgId());
                        }

                        @Override
                        public void onException(Throwable e) {
                            log.warn("Login log send failed tag={} err={}", tag, e.getMessage());
                        }
                    });
        } catch (Exception e) {
            // 异步日志失败不影响主流程
            log.warn("Login log publish error: {}", e.getMessage());
        }
    }
}

