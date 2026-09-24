package com.example.enterprise.user.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.result.Result;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class UserExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UserExceptionHandler.class);

    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handleNotLogin(NotLoginException e, HttpServletRequest request) {
        log.warn("NotLogin uri={}", request.getRequestURI());
        return Result.fail(ErrorCode.UNAUTHORIZED);
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handleNotPermission(NotPermissionException e, HttpServletRequest request) {
        log.warn("NotPermission uri={} perm={}", request.getRequestURI(), e.getPermission());
        return Result.fail(ErrorCode.FORBIDDEN);
    }

    @ExceptionHandler(NotRoleException.class)
    public Result<Void> handleNotRole(NotRoleException e, HttpServletRequest request) {
        log.warn("NotRole uri={} role={}", request.getRequestURI(), e.getRole());
        return Result.fail(ErrorCode.FORBIDDEN);
    }
}

