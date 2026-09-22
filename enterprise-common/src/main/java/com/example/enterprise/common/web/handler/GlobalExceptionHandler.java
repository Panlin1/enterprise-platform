package com.example.enterprise.common.web.handler;


import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.result.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * Global exception handler.
 * <p>
 * Production rule: never return full Java stack traces to the client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusiness(BusinessException e, HttpServletRequest request) {
        log.warn("BusinessException uri={} code={} msg={}", request.getRequestURI(), e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpServletRequest request) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (msg.isBlank()) {
            msg = ErrorCode.VALIDATION_FAILED.getMessage();
        }
        log.warn("Validation failed uri={} msg={}", request.getRequestURI(), msg);
        return Result.fail(ErrorCode.VALIDATION_FAILED.getCode(), msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBind(BindException e, HttpServletRequest request) {
        String msg = e.getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        if (msg.isBlank()) {
            msg = ErrorCode.VALIDATION_FAILED.getMessage();
        }
        log.warn("BindException uri={} msg={}", request.getRequestURI(), msg);
        return Result.fail(ErrorCode.VALIDATION_FAILED.getCode(), msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e, HttpServletRequest request) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        log.warn("ConstraintViolation uri={} msg={}", request.getRequestURI(), msg);
        return Result.fail(ErrorCode.VALIDATION_FAILED.getCode(), msg);
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public Result<Void> handleBadRequest(Exception e, HttpServletRequest request) {
        log.warn("Bad request uri={} msg={}", request.getRequestURI(), e.getMessage());
        return Result.fail(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("Method not allowed uri={} method={}", request.getRequestURI(), e.getMethod());
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), "请求方法不支持");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Result<Void> handleNoResource(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("Resource not found uri={}", request.getRequestURI());
        return Result.fail(ErrorCode.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<Void> handleIllegalArgument(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("IllegalArgument uri={} msg={}", request.getRequestURI(), e.getMessage());
        return Result.fail(ErrorCode.BAD_REQUEST.getCode(), e.getMessage() != null ? e.getMessage() : ErrorCode.BAD_REQUEST.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e, HttpServletRequest request) {
        // Full stack only in server logs — never in response body
        log.error("Unhandled exception uri={}", request.getRequestURI(), e);
        return Result.fail(ErrorCode.INTERNAL_ERROR);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }
}

