package com.example.enterprise.job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * job service entry point.
 *
 * Phase 1 only provides the runnable application skeleton.
 * Infrastructure integrations are introduced in later phases.
 */
@SpringBootApplication
public class EnterpriseJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseJobApplication.class, args);
    }
}
