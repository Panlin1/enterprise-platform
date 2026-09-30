package com.example.enterprise.auth.feign;

import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.common.feign.FeignUserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Auth → User service (Nacos service id: enterprise-user).
 * Internal APIs are not exposed via Gateway.
 */
@FeignClient(name = "enterprise-user", path = "/internal/users", contextId = "userFeignClient")
public interface UserFeignClient {

    @GetMapping("/by-username/{username}")
    Result<FeignUserDTO> getByUsername(@PathVariable("username") String username);

    @GetMapping("/{id}")
    Result<FeignUserDTO> getById(@PathVariable("id") Long id);
}
