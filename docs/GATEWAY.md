# Gateway 说明（Phase 9）

## 职责

| 能力      | 说明                                        |
|-----------|---------------------------------------------|
| 统一入口  | 端口 **8084**                               |
| 路由      | `/api/auth/**` → `lb://enterprise-auth`     |
|           | `/api/users/**` → `lb://enterprise-user`    |
|           | `/api/system/**` → `lb://enterprise-system` |
| 鉴权      | Sa-Token Reactor：除登录/验证码外需登录     |
| CORS      | 全局，支持前端跨域                          |
| RequestId | 自动生成/透传 `X-Request-Id`                |
| 负载均衡  | Spring Cloud LoadBalancer + Nacos           |

## 启动顺序

1. MySQL / Redis / Nacos  
2. enterprise-auth (8082)、enterprise-user (8081)、enterprise-system (8083)  
3. enterprise-gateway (8080)

```bash
mvn -pl enterprise-gateway -am package -DskipTests
mvn -pl enterprise-gateway spring-boot:run
```

## 联调示例（经网关）

```bash
# 验证码
curl -s http://localhost:8080/api/auth/captcha

# 登录
curl -s -X POST http://localhost:8080/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456","captchaId":"...","captcha":"..."}'

# 用户列表（需 Token）
curl -s 'http://localhost:8080/api/users?page=1&size=10' \
  -H 'Authorization: Bearer <token>'

# 菜单树
curl -s http://localhost:8080/api/system/menus/tree \
  -H 'Authorization: Bearer <token>'
```

## 无 Nacos 时

```bash
export NACOS_DISCOVERY_ENABLED=false
```

需将路由 `uri` 改为直连，例如：

```yaml
auth-uri: http://localhost:8081  # auth
user-uri: http://localhost:8082  # user
system-uri: http://localhost:8083  # system
```

## 注意

- Gateway 为 **WebFlux**，不要引入 `spring-boot-starter-web`
- Gateway 只做登录态校验；细粒度权限仍在各业务服务 `@SaCheckPermission`
- Token 与 Auth 共用 Redis（Sa-Token Redis 会话）
