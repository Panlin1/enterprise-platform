# enterprise-platform

Enterprise Java Web / Microservices Platform.

## Phase 0 + Phase 1

Current implementation scope:

- Java 17
- Spring Boot 3.5.x baseline
- Spring Cloud 2025.0.x baseline
- Spring Cloud Alibaba 2025.0.x baseline
- Maven multi-module project
- Gateway and business service skeletons
- No MySQL / Redis / Nacos / RocketMQ implementation yet

## Modules

- enterprise-common
- enterprise-gateway
- enterprise-auth
- enterprise-user
- enterprise-system
- enterprise-file
- enterprise-business
- enterprise-job

## Local validation

Requirements:

- JDK 17+
- Maven 3.6.3+

From the project root:

```bash
mvn -DskipTests clean package
```

Then run a service from its module directory, for example:

```bash
mvn spring-boot:run
```

Ports:

| Module | Port |
|---|---:|
| gateway | 8080 |
| auth | 8081 |
| user | 8082 |
| system | 8083 |
| file | 8084 |
| business | 8085 |
| job | 8086 |

Phase 1 deliberately does not connect to external middleware. Nacos, MySQL, Redis, Sentinel, RocketMQ and other infrastructure are introduced in their dedicated phases.
