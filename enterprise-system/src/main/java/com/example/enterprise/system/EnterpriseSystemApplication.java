package com.example.enterprise.system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.example.enterprise.system", "com.example.enterprise.common"})
@MapperScan("com.example.enterprise.system.mapper")
public class EnterpriseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseSystemApplication.class, args);
    }
}

