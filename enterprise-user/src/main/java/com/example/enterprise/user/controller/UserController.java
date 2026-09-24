package com.example.enterprise.user.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.user.dto.UserCreateDTO;
import com.example.enterprise.user.dto.UserPasswordDTO;
import com.example.enterprise.user.dto.UserQueryDTO;
import com.example.enterprise.user.dto.UserStatusDTO;
import com.example.enterprise.user.dto.UserUpdateDTO;
import com.example.enterprise.user.service.UserService;
import com.example.enterprise.user.vo.UserVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "用户分页")
    @GetMapping
    @SaCheckPermission("system:user:list")
    public Result<PageResult<UserVO>> page(@ModelAttribute UserQueryDTO query) {
        return Result.success(userService.page(query));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @SaCheckPermission("system:user:list")
    public Result<UserVO> detail(@PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @SaCheckPermission("system:user:add")
    public Result<Long> create(@Valid @RequestBody UserCreateDTO dto) {
        return Result.success(userService.create(dto));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    @SaCheckPermission("system:user:update")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserUpdateDTO dto) {
        userService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:user:delete")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }

    @Operation(summary = "修改状态")
    @PutMapping("/{id}/status")
    @SaCheckPermission("system:user:status")
    public Result<Void> updateStatus(@PathVariable Long id, @Valid @RequestBody UserStatusDTO dto) {
        userService.updateStatus(id, dto);
        return Result.success();
    }

    @Operation(summary = "修改/重置密码")
    @PutMapping("/{id}/password")
    @SaCheckPermission("system:user:password")
    public Result<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UserPasswordDTO dto) {
        userService.updatePassword(id, dto);
        return Result.success();
    }
}

