package com.example.enterprise.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 文件服务启动类
 * <p>
 *  端口默认 8085；
 *  提供上传、下载、删除与元数据查询。
 * </p>
 */
@SpringBootApplication(scanBasePackages = {"com.example.enterprise.file", "com.example.enterprise.common"})
@MapperScan("com.example.enterprise.file.mapper")
@EnableDiscoveryClient
public class EnterpriseFileApplication {

    public static void main(String[] args) {
        SpringApplication.run(EnterpriseFileApplication.class, args);
    }
}
