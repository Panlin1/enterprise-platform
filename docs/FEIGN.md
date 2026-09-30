# OpenFeign 说明（Phase 10）

## 目标

服务间 HTTP 调用统一使用 **OpenFeign + LoadBalancer + Nacos**，禁止硬编码 IP。

## 已实现调用链

```text
Auth (8082)
  └─ UserFeignClient
       └─ lb://enterprise-user
            └─ GET /internal/users/by-username/{username}
            └─ GET /internal/users/{id}
```

登录时凭据加载改为 Feign；角色/权限仍由 Auth 本地 Mapper 查询（同库只读）。

## 内部接口约定

| 路径           | 说明                                |
|----------------|-------------------------------------|
| `/internal/**` | 仅服务间调用，**不**挂 Gateway 路由 |
| User Sa-Token  | 已排除 `/internal/**`，免登录态     |

Gateway 仅转发 `/api/**`，因此 `/internal/**` 不会从公网经网关进入。

## 关键

| 类                       | 位置                             |
|--------------------------|----------------------------------|
| `FeignUserDTO`           | enterprise-common                |
| `UserFeignClient`        | enterprise-auth                  |
| `FeignConfig`            | 透传 Authorization、X-Request-Id |
| `InternalUserController` | enterprise-user                  |

## 配置

```yaml
spring:
  cloud:
    openfeign:
      client:
        config:
          default:
            connectTimeout: 3000
            readTimeout: 5000
```

## 验收

1. Nacos 中 `enterprise-auth`、`enterprise-user` 均在线  
2. 经 Gateway 或 Auth 直连登录成功  
3. Auth 日志出现 `via=feign`  
4. 关闭 user 服务后登录应提示「用户服务暂不可用」  

## 后续扩展

- Business / File 等模块按同样模式新增 `*FeignClient`
- 复杂错误可增加统一 `ErrorDecoder` 映射为 `BusinessException`
- 高可用可加 Resilience4j / Sentinel（Phase 11）
