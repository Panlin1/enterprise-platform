package com.example.enterprise.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * system service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseSystemApplication.class, args);
    }
}
