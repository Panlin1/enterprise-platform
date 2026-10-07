package com.example.enterprise.business.config;

import cn.dev33.satoken.exception.NotLoginException;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 业务服务认证异常处理。
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class BusinessExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(BusinessExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e, HttpServletRequest request) {
        log.warn("未登录 uri={}", request.getRequestURI());
        return Result.fail(ErrorCode.UNAUTHORIZED);
    }
}
