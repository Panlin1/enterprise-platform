# Phase 0 + Phase 1 验收清单

## Phase 0：版本与环境基线

- [ ] JDK 17+ 已安装
- [ ] Maven 3.6.3+ 已安装
- [ ] `java -version` 输出 Java 17 或更高版本
- [ ] `mvn -version` 使用的 Java 与项目要求一致
- [ ] 项目编码 UTF-8
- [ ] Windows 本地开发目录建议：`D:\workspace\enterprise-platform`
- [ ] 本阶段不要求 Docker
- [ ] 本阶段不要求启动 MySQL / Redis / Nacos / RocketMQ

## Phase 1：Maven 多模块骨架

- [ ] 根 `pom.xml` 可被 Maven 解析
- [ ] 8 个 Maven 模块均被 root module 管理
- [ ] Java 编译级别为 17
- [ ] Gateway 为独立 Spring Boot 应用
- [ ] auth/user/system/file/business/job 为独立 Spring Boot Web 应用
- [ ] common 为普通 jar
- [ ] 所有服务均有 `application.yml`
- [ ] 所有服务均提供 Actuator health
- [ ] `mvn -DskipTests clean package` 成功

## 推荐命令

```powershell
cd D:\workspace\enterprise-platform

java -version
mvn -version

mvn -DskipTests clean package
```

成功标志：Maven 输出 `BUILD SUCCESS`。

## 单服务启动

```powershell
cd enterprise-auth
mvn spring-boot:run
```

浏览器或 curl 访问：

```text
http://localhost:8081/actuator/health
```

预期返回：

```json
{"status":"UP"}
```

Gateway：

```text
http://localhost:8080/actuator/health
```

## 注意

Spring Cloud Alibaba 官方 2025.0.0.0 的明确版本矩阵是 Spring Boot 3.5.0 + Spring Cloud 2025.0.0，同时官方说明 2025.0.x 适配 Spring Boot 3.5.x / Spring Cloud 2025.0.x。

本项目当前根 POM 使用：

- Spring Boot 3.5.16
- Spring Cloud 2025.0.2
- Spring Cloud Alibaba 2025.0.0.0

该组合需要以本地 Maven 构建结果进行兼容性验证；如果 Maven 依赖解析或运行时出现兼容问题，应在进入 Phase 2 前固定到一组官方明确匹配的版本，而不是继续向后开发。
