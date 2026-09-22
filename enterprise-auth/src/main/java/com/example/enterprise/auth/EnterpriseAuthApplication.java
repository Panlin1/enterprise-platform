package com.example.enterprise.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * auth service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseAuthApplication.class, args);
    }
}
