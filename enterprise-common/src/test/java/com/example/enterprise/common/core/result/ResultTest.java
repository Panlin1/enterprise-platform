package com.example.enterprise.common.core.result;

import com.example.enterprise.common.core.constant.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultTest {

    @Test
    void successWithData() {
        Result<String> r = Result.success("ok");
        assertTrue(r.isSuccess());
        assertEquals(200, r.code());
        assertEquals("success", r.message());
        assertEquals("ok", r.data());
    }

    @Test
    void successEmpty() {
        Result<Void> r = Result.success();
        assertTrue(r.isSuccess());
        assertNull(r.data());
    }

    @Test
    void failWithErrorCode() {
        Result<Void> r = Result.fail(ErrorCode.UNAUTHORIZED);
        assertFalse(r.isSuccess());
        assertEquals(401, r.code());
        assertEquals(ErrorCode.UNAUTHORIZED.getMessage(), r.message());
        assertNull(r.data());
    }

    @Test
    void pageResultOf() {
        PageResult<String> page = PageResult.of(java.util.List.of("a", "b"), 2, 1, 20);
        assertEquals(2, page.records().size());
        assertEquals(2L, page.total());
        assertEquals(1L, page.page());
        assertEquals(20L, page.size());
    }
}

