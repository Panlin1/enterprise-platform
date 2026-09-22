package com.example.enterprise.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * business service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseBusinessApplication.class, args);
    }
}
