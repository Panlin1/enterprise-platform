# RocketMQ 说明（Phase 12）

## 目标

| 场景     | Topic                      | 生产者       | 消费者                   |
|----------|----------------------------|--------------|--------------------------|
| 登录日志 | `enterprise-login-log`     | Auth         | System → `sys_login_log` |
| 操作日志 | `enterprise-operation-log` | （预留 DTO） | （后续扩展）             |

## 版本

- 客户端 starter：`rocketmq-spring-boot-starter` **2.3.4**
- Broker：建议 **RocketMQ 5.1+ / 5.3.x**（与 SCA 组件表 RocketMQ 5.3.1 同代）

## 默认行为（重要）

**未激活 `mq` profile 时不连接 NameServer**，本地无 MQ 可正常启动 Auth/System。

启用 MQ：

```bash
# 1. 启动 NameServer + Broker（示例）
# 2. 服务增加 profile
mvn -pl enterprise-auth spring-boot:run -Dspring-boot.run.profiles=dev,mq
mvn -pl enterprise-system spring-boot:run -Dspring-boot.run.profiles=dev,mq
```

环境变量：`ROCKETMQ_NAME_SERVER=127.0.0.1:9876`

## Topic / Tag

| 常量                       | 值                   |
|----------------------------|----------------------|
| `MqTopics.TOPIC_LOGIN_LOG` | enterprise-login-log |
| `TAG_LOGIN_SUCCESS`        | LOGIN_SUCCESS        |
| `TAG_LOGIN_FAIL`           | LOGIN_FAIL           |
| `TAG_LOGOUT`               | LOGOUT               |

## 消息体

`LoginLogMessage`：userId、username、eventType、ip、userAgent、message、eventTime

## 本阶段文件清单（学习 / 修改入口）

见 README Phase 12 与下文「交付件路径」。

## 验收

1. 启动 RocketMQ NameServer + Broker  
2. Auth、System 使用 `dev,mq` profile  
3. 登录成功 → System 日志 `Login log saved` → 表 `sys_login_log` 有记录  
4. 不启用 `mq` profile 时，Auth/System 仍可启动、登录可用（仅无异步日志）  
