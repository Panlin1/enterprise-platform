package com.example.enterprise.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.common.feign.FeignUserDTO;
import com.example.enterprise.user.entity.SysUser;
import com.example.enterprise.user.mapper.SysUserMapper;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Internal APIs for service-to-service calls (Feign).
 * Not registered on Gateway routes — only reachable via Nacos lb://enterprise-user.
 */
@Hidden
@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final SysUserMapper userMapper;

    public InternalUserController(SysUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/by-username/{username}")
    public Result<FeignUserDTO> byUsername(@PathVariable String username) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
                .last("LIMIT 1"));
        if (user == null) {
            throw BusinessException.of(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(toDto(user));
    }

    @GetMapping("/{id}")
    public Result<FeignUserDTO> byId(@PathVariable Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.of(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(toDto(user));
    }

    private FeignUserDTO toDto(SysUser user) {
        FeignUserDTO dto = new FeignUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPassword(user.getPassword());
        dto.setNickname(user.getNickname());
        dto.setRealName(user.getRealName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setAvatar(user.getAvatar());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
