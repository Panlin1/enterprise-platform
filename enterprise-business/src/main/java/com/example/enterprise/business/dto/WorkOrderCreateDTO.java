package com.example.enterprise.business.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 创建工单请求。
 */
@Schema(description = "创建工单")
public class WorkOrderCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Size(max = 128, message = "标题最长128字符")
    private String title;

    @Size(max = 1024, message = "内容最长1024字符")
    private String content;

    /**
     * 优先级：1高 2中 3低，默认2
     */
    private Integer priority;

    private Long assigneeId;
    private String remark;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
