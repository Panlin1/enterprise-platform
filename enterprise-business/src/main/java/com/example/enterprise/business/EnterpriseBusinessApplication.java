package com.example.enterprise.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 业务服务启动类。
 * <p>
 * 端口默认 8086；本阶段提供示例工单域，可按真实业务替换扩展。
 */
@SpringBootApplication(scanBasePackages = {"com.example.enterprise.business", "com.example.enterprise.common"})
@MapperScan("com.example.enterprise.business.mapper")
@EnableDiscoveryClient
public class EnterpriseBusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseBusinessApplication.class, args);
    }
}
