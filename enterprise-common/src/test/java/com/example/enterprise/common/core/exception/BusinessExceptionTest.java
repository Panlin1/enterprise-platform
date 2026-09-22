package com.example.enterprise.common.core.exception;

import com.example.enterprise.common.core.constant.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BusinessExceptionTest {

    @Test
    void ofErrorCode() {
        BusinessException ex = BusinessException.of(ErrorCode.USER_NOT_FOUND);
        assertEquals(ErrorCode.USER_NOT_FOUND.getCode(), ex.getCode());
        assertEquals(ErrorCode.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void ofCustomMessage() {
        BusinessException ex = BusinessException.of(ErrorCode.BAD_REQUEST, "字段不能为空");
        assertEquals(400, ex.getCode());
        assertEquals("字段不能为空", ex.getMessage());
    }
}
