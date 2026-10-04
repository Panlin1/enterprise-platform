# Sentinel 说明（Phase 11）

## 目标

- 网关层：登录 / Auth 路径限流
- 服务层：Auth 登录、验证码 `@SentinelResource` 限流
- 统一限流响应：`code=429`

## 版本

与 Spring Cloud Alibaba **2025.0.0.0** 对齐，Sentinel **1.8.9**（BOM 管理）。

## 已接入模块

| 模块               | 能力                                                          |
|--------------------|---------------------------------------------------------------|
| enterprise-gateway | `spring-cloud-alibaba-sentinel-gateway` + 自定义 API 分组规则 |
| enterprise-auth    | `@SentinelResource` + 本地 FlowRule                           |

## 默认规则（代码内初始化）

### Gateway

| API 组         | 路径                   | 阈值       |
|----------------|------------------------|------------|
| auth-login-api | `/api/auth/login` 精确 | **10 QPS** |
| auth-api-group | `/api/auth/**` 前缀    | **50 QPS** |

超限返回 HTTP 429：

```json
{"code":429,"message":"请求过于频繁，请稍后再试","data":null}
```

### Auth 服务

| Resource       | 阈值   |
|----------------|--------|
| `auth:login`   | 20 QPS |
| `auth:captcha` | 30 QPS |

## Dashboard（可选）

```bash
# 官方 Sentinel Dashboard 需与客户端版本大致匹配（1.8.x）
# 建议监听 8858，避免与 Gateway 8080 冲突
java -Dserver.port=8858 -jar sentinel-dashboard-1.8.9.jar
```

应用配置：

```yaml
spring.cloud.sentinel.transport.dashboard: localhost:8858
```

环境变量：`SENTINEL_DASHBOARD`、`SENTINEL_CLIENT_PORT`。

无 Dashboard 时，本地代码规则仍生效，服务可正常启动。

## 验收

1. 启动 Gateway + Auth  
2. 短时间对 `POST /api/auth/login` 压测超过 10 QPS  
3. 出现 `429` / `请求过于频繁`  
4. （可选）Dashboard 能看到 `enterprise-gateway` / `enterprise-auth` 应用  

## 后续

- 规则持久化到 Nacos（`Sentinel` 配置 DataId）
- 对 Feign 调用加熔断（`@SentinelResource` 或 Feign 整合）
- 系统/用户接口按业务再加限流规则
