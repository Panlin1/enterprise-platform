package com.example.enterprise.auth.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.enterprise.auth.dto.LoginRequest;
import com.example.enterprise.auth.entity.AuthUser;
import com.example.enterprise.auth.mapper.AuthUserMapper;
import com.example.enterprise.auth.service.AuthService;
import com.example.enterprise.auth.vo.CaptchaVO;
import com.example.enterprise.auth.vo.LoginVO;
import com.example.enterprise.auth.vo.UserInfoVO;
import com.example.enterprise.common.core.constant.CommonConstants;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.redis.RedisConstants;
import com.example.enterprise.common.redis.RedisKeyBuilder;
import com.example.enterprise.common.redis.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private static final String CAPTCHA_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 4;

    private final AuthUserMapper authUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtils redisUtils;

    @Value("${auth.login.fail-max:5}")
    private int loginFailMax;

    @Value("${auth.captcha.dev-expose-code:true}")
    private boolean captchaDevExposeCode;

    public AuthServiceImpl(AuthUserMapper authUserMapper,
                           PasswordEncoder passwordEncoder,
                           RedisUtils redisUtils) {
        this.authUserMapper = authUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisUtils = redisUtils;
    }

    @Override
    public CaptchaVO createCaptcha() {
        String code = randomCaptchaCode();
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        String key = RedisKeyBuilder.authCaptcha(captchaId);
        redisUtils.setString(key, code.toLowerCase(), RedisConstants.CAPTCHA_TTL);

        String imageBase64 = renderCaptchaImage(code);
        String expose = captchaDevExposeCode ? code : null;
        return new CaptchaVO(captchaId, imageBase64, expose);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        String username = request.getUsername().trim();

        checkLoginLock(username);
        validateCaptcha(request.getCaptchaId(), request.getCaptcha());

        AuthUser user = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUser>()
                .eq(AuthUser::getUsername, username)
                .last("LIMIT 1"));

        if (user == null) {
            onLoginFail(username);
            throw BusinessException.of(ErrorCode.LOGIN_FAILED);
        }
        if (user.getStatus() == null || user.getStatus() != CommonConstants.STATUS_ENABLED) {
            throw BusinessException.of(ErrorCode.ACCOUNT_DISABLED);
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            onLoginFail(username);
            throw BusinessException.of(ErrorCode.LOGIN_FAILED);
        }

        clearLoginFail(username);

        StpUtil.login(user.getId());
        StpUtil.getSession().set("username", user.getUsername());
        StpUtil.getSession().set("nickname", user.getNickname());

        // warm permission cache
        List<String> permissions = authUserMapper.selectPermissionCodesByUserId(user.getId());
        redisUtils.setWithJitter(
                RedisKeyBuilder.userPermissions(user.getId()),
                permissions,
                RedisConstants.USER_PERMISSIONS_TTL
        );

        SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
        log.info("User login success userId={} username={}", user.getId(), username);
        return new LoginVO(tokenInfo.getTokenValue(), tokenInfo.getTokenTimeout());
    }

    @Override
    public void logout() {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            redisUtils.evict(RedisKeyBuilder.userPermissions(userId));
            StpUtil.logout();
            log.info("User logout userId={}", userId);
        }
    }

    @Override
    public UserInfoVO currentUser() {
        StpUtil.checkLogin();
        Long userId = StpUtil.getLoginIdAsLong();
        AuthUser user = authUserMapper.selectById(userId);
        if (user == null || (user.getDeleted() != null && user.getDeleted() == CommonConstants.DELETED_YES)) {
            throw BusinessException.of(ErrorCode.USER_NOT_FOUND);
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setRoles(authUserMapper.selectRoleCodesByUserId(userId));
        vo.setPermissions(authUserMapper.selectPermissionCodesByUserId(userId));
        return vo;
    }

    private void validateCaptcha(String captchaId, String captcha) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captcha)) {
            throw BusinessException.of(ErrorCode.CAPTCHA_INVALID);
        }
        String key = RedisKeyBuilder.authCaptcha(captchaId);
        String cached = redisUtils.getString(key);
        // one-time use
        redisUtils.delete(key);
        if (cached == null || !cached.equalsIgnoreCase(captcha.trim())) {
            throw BusinessException.of(ErrorCode.CAPTCHA_INVALID);
        }
    }

    private void checkLoginLock(String username) {
        String key = RedisKeyBuilder.authLoginFail(username);
        String val = redisUtils.getString(key);
        if (val != null) {
            try {
                long count = Long.parseLong(val);
                if (count >= loginFailMax) {
                    throw BusinessException.of(ErrorCode.LOGIN_LOCKED);
                }
            } catch (NumberFormatException ignored) {
                // ignore bad value
            }
        }
    }

    private void onLoginFail(String username) {
        String key = RedisKeyBuilder.authLoginFail(username);
        long count = redisUtils.incrementWithExpire(key, RedisConstants.LOGIN_FAIL_TTL);
        log.warn("Login fail username={} failCount={}", username, count);
        if (count >= loginFailMax) {
            throw BusinessException.of(ErrorCode.LOGIN_LOCKED);
        }
    }

    private void clearLoginFail(String username) {
        redisUtils.delete(RedisKeyBuilder.authLoginFail(username));
    }

    private String randomCaptchaCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        StringBuilder sb = new StringBuilder(CAPTCHA_LENGTH);
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    private String renderCaptchaImage(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        try {
            g.setColor(new Color(240, 240, 245));
            g.fillRect(0, 0, width, height);
            g.setFont(new Font("Arial", Font.BOLD, 24));
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
                g.drawString(String.valueOf(code.charAt(i)), 20 + i * 22, 28);
            }
            for (int i = 0; i < 6; i++) {
                g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
                g.drawLine(random.nextInt(width), random.nextInt(height),
                        random.nextInt(width), random.nextInt(height));
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            log.error("Render captcha failed", e);
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "验证码生成失败");
        } finally {
            g.dispose();
        }
    }
}

