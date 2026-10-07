# Actuator 说明（Phase 15）

## 目标

各微服务统一暴露 **健康检查、应用信息、基础指标**，为 Phase 16（Prometheus/Grafana）打基础。

## 统一约定

| 项 | 值 |
|----|-----|
| 基础路径 | `/actuator` |
| 开发暴露 | `health,info,metrics` |
| Gateway 额外 | `gateway` |
| 健康明细 | `show-details: when_authorized` |
| 探针 | `probes.enabled=true`（liveness/readiness） |

## 服务端点一览

| 服务 | 端口 | 健康检查 |
|------|------|----------|
| gateway | 8080 | http://localhost:8080/actuator/health |
| user | 8081 | http://localhost:8081/actuator/health |
| auth | 8082 | http://localhost:8082/actuator/health |
| system | 8083 | http://localhost:8083/actuator/health |
| file | 8084 | http://localhost:8084/actuator/health |
| business | 8085 | http://localhost:8085/actuator/health |

常用路径：

```text
/actuator/health
/actuator/health/liveness
/actuator/health/readiness
/actuator/info
/actuator/metrics
```

Gateway 另有：`/actuator/gateway/routes`（需已暴露 gateway 端点）。

## 安全建议

- 生产环境不要对公网裸暴露 `/actuator/**`
- 可用网关黑名单、Spring Security、或仅内网访问
- `show-details` 保持 `when_authorized` 或 `never`

## 验收

1. 各服务启动后访问 `/actuator/health` 返回 `{"status":"UP"}`（依赖 DB/Redis 就绪）
2. `/actuator/info` 可访问
3. `/actuator/metrics` 可访问
