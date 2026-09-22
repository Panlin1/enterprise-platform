package com.example.enterprise.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Entry point for common beans when used as a library.
 * Business modules should either:
 * <ul>
 *   <li>depend on this jar and use {@code @SpringBootApplication(scanBasePackages = "com.example")}</li>
 *   <li>or {@code @Import(CommonAutoConfiguration.class)}</li>
 * </ul>
 */
@AutoConfiguration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@ComponentScan(basePackages = "com.example.enterprise.common")
public class CommonAutoConfiguration {
}
