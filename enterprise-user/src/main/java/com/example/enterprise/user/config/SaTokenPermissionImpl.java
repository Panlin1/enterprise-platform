package com.example.enterprise.user.config;

import cn.dev33.satoken.stp.StpInterface;

import com.example.enterprise.common.redis.RedisConstants;
import com.example.enterprise.common.redis.RedisKeyBuilder;
import com.example.enterprise.common.redis.RedisUtils;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Load roles/permissions for @SaCheckPermission on user service.
 */
@Component
public class SaTokenPermissionImpl implements StpInterface {

    private final JdbcTemplate jdbcTemplate;
    private final RedisUtils redisUtils;

    public SaTokenPermissionImpl(JdbcTemplate jdbcTemplate, RedisUtils redisUtils) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisUtils = redisUtils;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(String.valueOf(loginId));
        String key = RedisKeyBuilder.userPermissions(userId);
        List<String> cached = redisUtils.get(key);
        if (cached != null) {
            return cached;
        }
        List<String> permissions = jdbcTemplate.queryForList("""
                SELECT p.permission_code
                FROM sys_user_role ur
                INNER JOIN sys_role_permission rp ON ur.role_id = rp.role_id
                INNER JOIN sys_permission p ON rp.permission_id = p.id
                INNER JOIN sys_role r ON ur.role_id = r.id
                WHERE ur.user_id = ?
                  AND p.deleted = 0 AND p.status = 1
                  AND r.deleted = 0 AND r.status = 1
                """, String.class, userId);
        if (permissions == null) {
            permissions = Collections.emptyList();
        }
        redisUtils.setWithJitter(key, new ArrayList<>(permissions), RedisConstants.USER_PERMISSIONS_TTL);
        return permissions;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(String.valueOf(loginId));
        List<String> roles = jdbcTemplate.queryForList("""
                SELECT r.role_code
                FROM sys_user_role ur
                INNER JOIN sys_role r ON ur.role_id = r.id
                WHERE ur.user_id = ? AND r.deleted = 0 AND r.status = 1
                """, String.class, userId);
        return roles == null ? Collections.emptyList() : roles;
    }
}
