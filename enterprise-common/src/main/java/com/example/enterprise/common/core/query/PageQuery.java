package com.example.enterprise.common.core.query;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;

/**
 * Common pagination query parameters.
 * Bind with {@code @ModelAttribute} or request params: page, size, orderBy, orderDir.
 */
public class PageQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final long DEFAULT_PAGE = 1L;
    public static final long DEFAULT_SIZE = 20L;
    public static final long MAX_SIZE = 200L;

    /**
     * Page number (1-based)
     */
    @Min(value = 1, message = "页码最小为 1")
    private Long page = DEFAULT_PAGE;

    /**
     * Page size
     */
    @Min(value = 1, message = "每页条数最小为 1")
    @Max(value = 200, message = "每页条数最大为 200")
    private Long size = DEFAULT_SIZE;

    /**
     * Optional order column (whitelist in service layer)
     */
    private String orderBy;

    /**
     * asc / desc
     */
    private String orderDir = "desc";

    public Long getPage() {
        return page == null || page < 1 ? DEFAULT_PAGE : page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        if (size == null || size < 1) {
            return DEFAULT_SIZE;
        }
        return Math.min(size, MAX_SIZE);
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderDir() {
        return orderDir;
    }

    public void setOrderDir(String orderDir) {
        this.orderDir = orderDir;
    }

    /**
     * Build MyBatis-Plus {@link Page} without order.
     */
    public <T> Page<T> toPage() {
        return new Page<>(getPage(), getSize());
    }

    /**
     * Build MyBatis-Plus {@link Page} with order if column is provided.
     * Caller must ensure {@code orderBy} is a safe column name (whitelist).
     */
    public <T> Page<T> toPage(String safeOrderColumn) {
        Page<T> p = toPage();
        if (safeOrderColumn != null && !safeOrderColumn.isBlank()) {
            boolean asc = "asc".equalsIgnoreCase(orderDir);
            p.addOrder(asc ? OrderItem.asc(safeOrderColumn) : OrderItem.desc(safeOrderColumn));
        }
        return p;
    }
}
