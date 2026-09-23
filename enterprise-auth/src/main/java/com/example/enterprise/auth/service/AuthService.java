package com.example.enterprise.auth.service;


import com.example.enterprise.auth.dto.LoginRequest;
import com.example.enterprise.auth.vo.CaptchaVO;
import com.example.enterprise.auth.vo.LoginVO;
import com.example.enterprise.auth.vo.UserInfoVO;

public interface AuthService {

    CaptchaVO createCaptcha();

    LoginVO login(LoginRequest request);

    void logout();

    UserInfoVO currentUser();
}

