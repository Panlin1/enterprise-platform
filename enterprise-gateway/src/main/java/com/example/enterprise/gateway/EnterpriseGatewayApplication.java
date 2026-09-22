package com.example.enterprise.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * gateway service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseGatewayApplication.class, args);
    }
}
