package com.example.enterprise.auth.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import com.example.enterprise.auth.config.SentinelAuthConfig;
import com.example.enterprise.auth.dto.LoginRequest;
import com.example.enterprise.auth.service.AuthService;
import com.example.enterprise.auth.vo.CaptchaVO;
import com.example.enterprise.auth.vo.LoginVO;
import com.example.enterprise.auth.vo.UserInfoVO;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.result.Result;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;


@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "获取验证码")
    @GetMapping("/captcha")
    @SentinelResource(value = SentinelAuthConfig.RES_CAPTCHA, blockHandler = "captchaBlock")
    public Result<CaptchaVO> captcha() {
        return Result.success(authService.createCaptcha());
    }

    public Result<CaptchaVO> captchaBlock(BlockException ex) {
        throw BusinessException.of(ErrorCode.TOO_MANY_REQUESTS);
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    @SentinelResource(value = SentinelAuthConfig.RES_LOGIN, blockHandler = "loginBlock")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }

    public Result<LoginVO> loginBlock(LoginRequest request, BlockException ex) {
        throw BusinessException.of(ErrorCode.TOO_MANY_REQUESTS);
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public Result<UserInfoVO> me() {
        return Result.success(authService.currentUser());
    }
}
