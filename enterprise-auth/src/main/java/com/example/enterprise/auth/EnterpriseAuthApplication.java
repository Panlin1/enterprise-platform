package com.example.enterprise.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.mybatis.spring.annotation.MapperScan;

/**
 * Auth service entry.
 * <p>
 * Phase 5: local MySQL for credential check.
 * Phase 10+: prefer Feign to enterprise-user for user profile.
 */
@SpringBootApplication(scanBasePackages = {"com.example.enterprise.auth", "com.example.enterprise.common"})
@MapperScan("com.example.enterprise.auth.mapper")
public class EnterpriseAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseAuthApplication.class, args);
    }
}
