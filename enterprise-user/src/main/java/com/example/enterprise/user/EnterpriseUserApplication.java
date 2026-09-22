package com.example.enterprise.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * user service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseUserApplication.class, args);
    }
}
