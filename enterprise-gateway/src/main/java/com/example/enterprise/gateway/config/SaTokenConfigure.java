package com.example.enterprise.gateway.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway-level Sa-Token filter: login required except whitelist paths.
 * Downstream services still apply fine-grained {@code @SaCheckPermission}.
 */
@Configuration
public class SaTokenConfigure {

    @Bean
    public SaReactorFilter saReactorFilter() {
        return new SaReactorFilter()
                .addInclude("/**")
                .addExclude(
                        "/api/auth/login",
                        "/api/auth/captcha",
                        "/actuator/**",
                        "/favicon.ico"
                )
                .setAuth(obj -> SaRouter.match("/**", r -> StpUtil.checkLogin()))
                .setError(e -> {
                    if (e instanceof NotLoginException) {
                        return SaResult.code(401).setMsg("未登录或登录已过期");
                    }
                    return SaResult.error(e.getMessage());
                });
    }
}
