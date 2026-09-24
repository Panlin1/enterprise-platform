package com.example.enterprise.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "修改用户状态")
public class UserStatusDTO {

    @NotNull(message = "状态不能为空")
    @Schema(description = "0 禁用 1 启用")
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
