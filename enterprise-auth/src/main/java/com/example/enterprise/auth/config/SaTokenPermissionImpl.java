package com.example.enterprise.auth.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.enterprise.auth.mapper.AuthUserMapper;
import com.example.enterprise.common.redis.RedisConstants;
import com.example.enterprise.common.redis.RedisKeyBuilder;
import com.example.enterprise.common.redis.RedisUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Sa-Token permission / role loader.
 * Loads from Redis cache first, then DB.
 */
@Component
public class SaTokenPermissionImpl implements StpInterface {

    private final AuthUserMapper authUserMapper;
    private final RedisUtils redisUtils;

    public SaTokenPermissionImpl(AuthUserMapper authUserMapper, RedisUtils redisUtils) {
        this.authUserMapper = authUserMapper;
        this.redisUtils = redisUtils;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = toUserId(loginId);
        String key = RedisKeyBuilder.userPermissions(userId);
        List<String> cached = redisUtils.get(key);
        if (cached != null) {
            return cached;
        }
        List<String> permissions = authUserMapper.selectPermissionCodesByUserId(userId);
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        redisUtils.setWithJitter(key, new ArrayList<>(permissions), RedisConstants.USER_PERMISSIONS_TTL);
        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = toUserId(loginId);
        List<String> roles = authUserMapper.selectRoleCodesByUserId(userId);
        return roles == null ? Collections.emptyList() : roles;
    }

    private Long toUserId(Object loginId) {
        if (loginId instanceof Long l) {
            return l;
        }
        return Long.parseLong(String.valueOf(loginId));
    }
}
