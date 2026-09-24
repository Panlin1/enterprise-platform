# Nacos 接入说明（Phase 8）

## 1. 职责

| 能力 | 用途 |
|------|------|
| 服务注册发现 | auth / user / system（后续 gateway）互相发现 |
| 配置中心 | 共享 MySQL/Redis、各服务专属配置；敏感项不进 Git |

## 2. 版本

与父工程一致：

- Spring Cloud Alibaba **2025.0.0.0**
- Nacos Server 建议 **2.x / 3.0.x**（与 SCA 文档对齐，开发常用 2.2+ / 3.0.3）

## 3. 本地启动 Nacos

```bash
# 示例：单机 Docker（按你本机镜像调整）
docker run -d --name nacos -p 8848:8848 -p 9848:9848 \
  -e MODE=standalone \
  nacos/nacos-server:v2.4.3

# 控制台
# http://localhost:8848/nacos
# 默认账号 nacos / nacos
```

## 4. 服务侧配置

依赖（已加入 auth / user / system）：

- `spring-cloud-starter-alibaba-nacos-discovery`
- `spring-cloud-starter-alibaba-nacos-config`
- `spring-cloud-starter-bootstrap`

环境变量：

| 变量 | 默认 | 说明 |
|------|------|------|
| `NACOS_ADDR` | localhost:8848 | 地址 |
| `NACOS_NAMESPACE` | 空 | 命名空间 ID |
| `NACOS_DISCOVERY_ENABLED` | true | 是否注册 |
| `NACOS_CONFIG_ENABLED` | false | 是否拉远程配置（本地默认可关） |

服务名：

```text
enterprise-auth
enterprise-user
enterprise-system
```

## 5. 推荐配置拆分

```text
Nacos
├── common-mysql.yml
├── common-redis.yml
├── enterprise-auth.yml
├── enterprise-user.yml
└── enterprise-system.yml
```

示例见：`docs/config/nacos-shared-example.yml`

开启远程配置：

```bash
export NACOS_CONFIG_ENABLED=true
```

## 6. 验收

1. 启动 Nacos  
2. 启动 auth / user / system  
3. 控制台 **服务管理 → 服务列表** 看到三个实例  
4. 健康状态为健康  

无 Nacos 时：可将 `NACOS_DISCOVERY_ENABLED=false`，服务仍可按直连端口联调（与 Phase 5–7 相同）。

## 7. 注意

- 密码等敏感配置禁止提交 Git，用环境变量或 Nacos 配置  
- Gateway（Phase 9）将通过 `lb://enterprise-user` 等服务名路由  
- 本阶段不实现 Feign（Phase 10）
