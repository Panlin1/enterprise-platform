package com.example.enterprise.business.dto;

import com.example.enterprise.common.core.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 工单分页查询条件。
 */
@Schema(description = "工单分页查询")
public class WorkOrderQueryDTO extends PageQuery {

    private String title;
    private Integer status;
    private Integer priority;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
