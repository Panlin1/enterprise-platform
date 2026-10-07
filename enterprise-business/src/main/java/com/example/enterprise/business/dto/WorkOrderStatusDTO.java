package com.example.enterprise.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * 工单状态变更请求。
 */
@Schema(description = "工单状态变更")
public class WorkOrderStatusDTO {

    /**
     * 目标状态：0待处理 1处理中 2已完成 3已关闭
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    private String remark;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
