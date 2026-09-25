package com.example.enterprise.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.system.entity.SysMenu;
import com.example.enterprise.system.service.MenuService;
import com.example.enterprise.system.vo.MenuVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/system/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @Operation(summary = "菜单树")
    @GetMapping("/tree")
    @SaCheckPermission("system:menu:list")
    public Result<List<MenuVO>> tree() {
        return Result.success(menuService.tree());
    }

    @Operation(summary = "菜单列表")
    @GetMapping
    @SaCheckPermission("system:menu:list")
    public Result<List<MenuVO>> list() {
        return Result.success(menuService.listAll());
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @SaCheckPermission("system:menu:add")
    public Result<Long> create(@RequestBody SysMenu menu) {
        return Result.success(menuService.create(menu));
    }

    @Operation(summary = "修改菜单")
    @PutMapping("/{id}")
    @SaCheckPermission("system:menu:update")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setId(id);
        menuService.update(menu);
        return Result.success();
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:menu:delete")
    public Result<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return Result.success();
    }
}

