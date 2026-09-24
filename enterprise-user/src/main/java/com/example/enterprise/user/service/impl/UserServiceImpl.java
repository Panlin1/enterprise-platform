package com.example.enterprise.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.common.redis.RedisKeyBuilder;
import com.example.enterprise.common.redis.RedisUtils;
import com.example.enterprise.user.converter.UserConverter;
import com.example.enterprise.user.dto.UserCreateDTO;
import com.example.enterprise.user.dto.UserPasswordDTO;
import com.example.enterprise.user.dto.UserQueryDTO;
import com.example.enterprise.user.dto.UserStatusDTO;
import com.example.enterprise.user.dto.UserUpdateDTO;
import com.example.enterprise.user.entity.SysUser;
import com.example.enterprise.user.entity.SysUserRole;
import com.example.enterprise.user.mapper.SysUserMapper;
import com.example.enterprise.user.mapper.SysUserRoleMapper;
import com.example.enterprise.user.service.UserService;
import com.example.enterprise.user.vo.UserVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final SysUserMapper userMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final PasswordEncoder passwordEncoder;
    private final RedisUtils redisUtils;

    @Value("${user.init-password:123456}")
    private String initPassword;

    public UserServiceImpl(SysUserMapper userMapper,
                           SysUserRoleMapper userRoleMapper,
                           PasswordEncoder passwordEncoder,
                           RedisUtils redisUtils) {
        this.userMapper = userMapper;
        this.userRoleMapper = userRoleMapper;
        this.passwordEncoder = passwordEncoder;
        this.redisUtils = redisUtils;
    }

    @Override
    public PageResult<UserVO> page(UserQueryDTO query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(query.getUsername()), SysUser::getUsername, query.getUsername())
                .like(StringUtils.hasText(query.getPhone()), SysUser::getPhone, query.getPhone())
                .eq(query.getStatus() != null, SysUser::getStatus, query.getStatus())
                .orderByDesc(SysUser::getId);

        Page<SysUser> page = userMapper.selectPage(query.toPage(), wrapper);
        List<UserVO> records = new ArrayList<>();
        for (SysUser user : page.getRecords()) {
            records.add(UserConverter.toVO(
                    user,
                    userMapper.selectRoleIdsByUserId(user.getId()),
                    userMapper.selectRoleCodesByUserId(user.getId())
            ));
        }
        return PageResult.of(page, records);
    }

    @Override
    public UserVO getById(Long id) {
        SysUser user = requireUser(id);
        return UserConverter.toVO(
                user,
                userMapper.selectRoleIdsByUserId(id),
                userMapper.selectRoleCodesByUserId(id)
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(UserCreateDTO dto) {
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername()));
        if (exists != null && exists > 0) {
            throw BusinessException.of(ErrorCode.USERNAME_EXISTS);
        }

        SysUser user = UserConverter.toEntity(dto);
        String raw = StringUtils.hasText(dto.getPassword()) ? dto.getPassword() : initPassword;
        user.setPassword(passwordEncoder.encode(raw));
        userMapper.insert(user);

        bindRoles(user.getId(), dto.getRoleIds());
        log.info("User created id={} username={}", user.getId(), user.getUsername());
        return user.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, UserUpdateDTO dto) {
        SysUser user = requireUser(id);
        UserConverter.merge(user, dto);
        userMapper.updateById(user);

        if (dto.getRoleIds() != null) {
            bindRoles(id, dto.getRoleIds());
            evictPermissionCache(id);
        }
        redisUtils.evict(RedisKeyBuilder.userDetail(id));
        log.info("User updated id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireUser(id);
        // protect admin id=1
        if (id == 1L) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "不能删除内置管理员");
        }
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
        redisUtils.evict(RedisKeyBuilder.userDetail(id));
        evictPermissionCache(id);
        log.info("User deleted id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long id, UserStatusDTO dto) {
        if (dto.getStatus() == null || (dto.getStatus() != 0 && dto.getStatus() != 1)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "状态只能为 0 或 1");
        }
        if (id == 1L && dto.getStatus() == 0) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "不能禁用内置管理员");
        }
        SysUser user = requireUser(id);
        user.setStatus(dto.getStatus());
        userMapper.updateById(user);
        redisUtils.evict(RedisKeyBuilder.userDetail(id));
        log.info("User status updated id={} status={}", id, dto.getStatus());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long id, UserPasswordDTO dto) {
        SysUser user = requireUser(id);
        if (StringUtils.hasText(dto.getOldPassword())) {
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw BusinessException.of(ErrorCode.OLD_PASSWORD_WRONG);
            }
        }
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw BusinessException.of(ErrorCode.PASSWORD_SAME);
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userMapper.updateById(user);
        log.info("User password updated id={}", id);
    }

    private SysUser requireUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw BusinessException.of(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    private void bindRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        for (Long roleId : roleIds) {
            if (roleId == null) {
                continue;
            }
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            ur.setCreatedAt(LocalDateTime.now());
            userRoleMapper.insert(ur);
        }
    }

    private void evictPermissionCache(Long userId) {
        redisUtils.evict(RedisKeyBuilder.userPermissions(userId));
    }
}

