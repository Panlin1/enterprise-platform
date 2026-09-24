package com.example.enterprise.user.dto;

import com.example.enterprise.common.core.query.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "用户分页查询")
public class UserQueryDTO extends PageQuery {

    private String username;
    private String phone;
    private Integer status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

