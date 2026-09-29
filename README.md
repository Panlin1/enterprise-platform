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

| Module   | Port |
|----------|-----:|
| auth     | 8081 |
| user     | 8082 |
| system   | 8083 |
| gateway  | 8084 |
| file     | 8085 |
| business | 8086 |
| job      | 8087 |

Phase 1 deliberately does not connect to external middleware. Nacos, MySQL, Redis, Sentinel, RocketMQ and other infrastructure are introduced in their dedicated phases.
