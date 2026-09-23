# Redis 使用规范

## 1. 角色

Redis 在本项目中不仅做缓存，还承担：

| 场景 | 说明 | 典型 Key |
|------|------|----------|
| 登录 Token / Session | Sa-Token + Redis（Auth 阶段） | 由 Sa-Token 管理；业务侧可用 `enterprise:auth:token:{userId}` 辅助 |
| 验证码 | 短 TTL | `enterprise:auth:captcha:{uuid}` |
| 登录失败计数 | 限流 / 锁定 | `enterprise:auth:login_fail:{username}` |
| 用户详情缓存 | Cache-Aside | `enterprise:user:detail:{userId}` |
| 用户权限缓存 | 登录后加载 | `enterprise:user:permissions:{userId}` |
| 字典缓存 | 只读热点 | `enterprise:system:dict:{dictType}` |
| 接口限流计数 | 网关 / Sentinel 前 | `enterprise:rate_limit:login:{username}` |
| 分布式锁 | 定时任务、防重 | 业务自定义，必须设 TTL |
| 防重复提交 | 短 TTL 锁 | 业务自定义 |

## 2. Key 规范

统一格式：

```text
{system}:{module}:{business}:{identifier}
```

`system` 固定为 `enterprise`（见 `CommonConstants.REDIS_KEY_PREFIX`）。

使用 `RedisKeyBuilder` 生成，禁止手写拼接散落在业务代码中。

| 方法 | 生成 Key |
|------|----------|
| `authToken(userId)` | `enterprise:auth:token:{userId}` |
| `authCaptcha(uuid)` | `enterprise:auth:captcha:{uuid}` |
| `authLoginFail(username)` | `enterprise:auth:login_fail:{username}` |
| `rateLimitLogin(username)` | `enterprise:rate_limit:login:{username}` |
| `userDetail(userId)` | `enterprise:user:detail:{userId}` |
| `userPermissions(userId)` | `enterprise:user:permissions:{userId}` |
| `systemDict(dictType)` | `enterprise:system:dict:{dictType}` |

## 3. TTL 约定（`RedisConstants`）

| 常量 | 默认 | 用途 |
|------|------|------|
| `CAPTCHA_TTL` | 5 分钟 | 验证码 |
| `LOGIN_FAIL_TTL` | 15 分钟 | 登录失败窗口 |
| `TOKEN_TTL` | 2 小时 | 业务侧 token 辅助缓存 |
| `USER_DETAIL_TTL` | 30 分钟 | 用户详情 |
| `USER_PERMISSIONS_TTL` | 30 分钟 | 权限集合 |
| `DICT_TTL` | 1 小时 | 字典 |
| `NULL_VALUE_TTL` | 2 分钟 | 空值占位（防穿透） |
| `TTL_JITTER_SECONDS` | 0–60 秒随机 | 防雪崩 |

## 4. 缓存读写模式

### 4.1 Cache-Aside（推荐）

```text
读:
  Redis → hit → 返回
       → miss → DB → 写 Redis（带 jitter TTL）→ 返回

写/删:
  先更新 DB → 再 delete 缓存（失败可重试或延迟双删）
```

代码示例：

```java
User user = redisUtils.getOrLoad(
    RedisKeyBuilder.userDetail(id),
    RedisConstants.USER_DETAIL_TTL,
    () -> userMapper.selectById(id),
    User.class
);

// 更新后
userMapper.updateById(user);
redisUtils.evict(RedisKeyBuilder.userDetail(user.getId()));
```

### 4.2 登录失败计数

```java
String key = RedisKeyBuilder.authLoginFail(username);
long failCount = redisUtils.incrementWithExpire(key, RedisConstants.LOGIN_FAIL_TTL);
if (failCount >= 5) {
    throw BusinessException.of(ErrorCode.LOGIN_LOCKED);
}
// 登录成功后
redisUtils.delete(key);
```

### 4.3 验证码

```java
String key = RedisKeyBuilder.authCaptcha(uuid);
redisUtils.setString(key, code, RedisConstants.CAPTCHA_TTL);
// 校验后立即 delete，防止重放
```

## 5. 三大问题与对策

| 问题 | 现象 | 本项目对策 |
|------|------|------------|
| **穿透** | 查不存在的数据打穿到 DB | `getOrLoad` 缓存 `__NULL__` 占位，短 TTL |
| **击穿** | 热点 Key 过期瞬间大量打 DB | 互斥锁（`setIfAbsent`）或逻辑过期；热点权限/字典尽量预热 |
| **雪崩** | 大量 Key 同时过期 | `setWithJitter` / `ttlWithJitter` 打散过期时间；核心数据多级缓存；限流 |

## 6. 一致性

1. **优先**：先写 MySQL，成功后再删 Redis（Cache-Aside）。
2. 删除失败：记录日志，可异步重试；不要先删缓存再写库（并发读会回填旧值）。
3. 权限变更后：必须 `evict(userPermissions(userId))`，必要时强制重新登录。
4. **禁止**把 Redis 当唯一数据源（除验证码、限流计数等纯临时状态）。

## 7. 安全

禁止写入 Redis 的内容：

- 明文密码
- 完整支付敏感信息
- 未脱敏的身份证号

Token 仅存服务端会话所需最小字段；日志中不打印 Token 全文。

## 8. 配置

本地开发片段：`docs/config/redis-dev.yml`

```yaml
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: 0
```

环境变量：

```text
REDIS_HOST
REDIS_PORT
REDIS_PASSWORD
REDIS_DATABASE
```

生产密码通过环境变量或 Nacos 配置中心注入，**禁止提交到 Git**。

## 9. 公共代码位置

| 类 | 职责 |
|----|------|
| `RedisConfig` | RedisTemplate JSON 序列化（有 Redis 连接才生效） |
| `RedisUtils` | get/set/delete、getOrLoad、jitter、计数 |
| `RedisKeyBuilder` | 统一 Key |
| `RedisConstants` | TTL 与空值占位符 |

业务服务依赖 `enterprise-common` 且配置了 `spring.data.redis` 后自动可用。

## 10. 本地启动 Redis（开发）

```bash
# Docker
docker run -d --name redis -p 6379:6379 redis:7-alpine

# 或本机
redis-server
redis-cli ping   # PONG
```

## 11. 验收检查

- [x] Key 命名统一且由 `RedisKeyBuilder` 生成
- [x] TTL 常量集中管理
- [x] 提供防穿透 / 防雪崩工具方法
- [x] 无 Redis 时服务仍可启动（条件装配）
- [x] 配置使用环境变量，无硬编码密码
- [x] 文档说明三大缓存问题与一致性策略

Auth 登录态与 Sa-Token Redis 集成在 **Phase 5** 完成。
