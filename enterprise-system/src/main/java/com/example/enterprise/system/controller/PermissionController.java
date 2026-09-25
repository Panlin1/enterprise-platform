package com.example.enterprise.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.system.service.PermissionService;
import com.example.enterprise.system.vo.PermissionVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "权限元数据")
@RestController
@RequestMapping("/api/system/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Operation(summary = "权限列表")
    @GetMapping
    @SaCheckPermission("system:permission:list")
    public Result<List<PermissionVO>> list() {
        return Result.success(permissionService.listAll());
    }
}
