package com.example.enterprise.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.example.enterprise.common.core.query.PageQuery;
import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.system.entity.SysConfig;
import com.example.enterprise.system.service.ConfigService;
import com.example.enterprise.system.vo.ConfigVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "参数配置")
@RestController
@RequestMapping("/api/system/configs")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @Operation(summary = "配置分页")
    @GetMapping
    @SaCheckPermission("system:config:list")
    public Result<PageResult<ConfigVO>> page(PageQuery query,
                                             @RequestParam(required = false) String configKey) {
        return Result.success(configService.page(query, configKey));
    }

    @Operation(summary = "按键取值")
    @GetMapping("/value/{configKey}")
    @SaCheckPermission("system:config:list")
    public Result<String> getValue(@PathVariable String configKey) {
        return Result.success(configService.getValue(configKey));
    }

    @Operation(summary = "新增配置")
    @PostMapping
    @SaCheckPermission("system:config:list")
    public Result<Long> create(@RequestBody SysConfig config) {
        return Result.success(configService.create(config));
    }

    @Operation(summary = "修改配置")
    @PutMapping("/{id}")
    @SaCheckPermission("system:config:list")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysConfig config) {
        config.setId(id);
        configService.update(config);
        return Result.success();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:config:list")
    public Result<Void> delete(@PathVariable Long id) {
        configService.delete(id);
        return Result.success();
    }
}

