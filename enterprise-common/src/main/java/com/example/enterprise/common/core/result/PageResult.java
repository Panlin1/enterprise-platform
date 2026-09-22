package com.example.enterprise.common.core.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Unified pagination payload placed inside {@link Result#data()}.
 *
 * <pre>
 * {
 *   "records": [],
 *   "total": 100,
 *   "page": 1,
 *   "size": 20
 * }
 * </pre>
 *
 * @param <T> record type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PageResult<T>(
        List<T> records,
        Long total,
        Long page,
        Long size
) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static <T> PageResult<T> empty(long page, long size) {
        return new PageResult<>(Collections.emptyList(), 0L, page, size);
    }

    public static <T> PageResult<T> of(List<T> records, long total, long page, long size) {
        return new PageResult<>(
                records == null ? Collections.emptyList() : records,
                total,
                page,
                size
        );
    }

    /**
     * Convert MyBatis-Plus {@link IPage} to {@link PageResult}.
     */
    public static <T> PageResult<T> of(IPage<T> pageData) {
        if (pageData == null) {
            return empty(1, 20);
        }
        return new PageResult<>(
                pageData.getRecords() == null ? Collections.emptyList() : pageData.getRecords(),
                pageData.getTotal(),
                pageData.getCurrent(),
                pageData.getSize()
        );
    }

    /**
     * Convert MyBatis-Plus page of entity to VO list with external mapping.
     */
    public static <E, V> PageResult<V> of(IPage<E> pageData, List<V> voList) {
        if (pageData == null) {
            return empty(1, 20);
        }
        return new PageResult<>(
                voList == null ? Collections.emptyList() : voList,
                pageData.getTotal(),
                pageData.getCurrent(),
                pageData.getSize()
        );
    }
}
