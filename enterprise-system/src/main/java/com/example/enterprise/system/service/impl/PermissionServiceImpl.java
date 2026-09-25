package com.example.enterprise.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.example.enterprise.system.entity.SysPermission;
import com.example.enterprise.system.mapper.SysPermissionMapper;
import com.example.enterprise.system.service.PermissionService;
import com.example.enterprise.system.vo.PermissionVO;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final SysPermissionMapper permissionMapper;

    public PermissionServiceImpl(SysPermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    @Override
    public List<PermissionVO> listAll() {
        List<SysPermission> list = permissionMapper.selectList(new LambdaQueryWrapper<SysPermission>()
                .orderByAsc(SysPermission::getSort)
                .orderByAsc(SysPermission::getId));
        return list.stream().map(p -> {
            PermissionVO vo = new PermissionVO();
            vo.setId(p.getId());
            vo.setPermissionCode(p.getPermissionCode());
            vo.setPermissionName(p.getPermissionName());
            vo.setPermissionType(p.getPermissionType());
            vo.setParentId(p.getParentId());
            vo.setSort(p.getSort());
            vo.setStatus(p.getStatus());
            return vo;
        }).collect(Collectors.toList());
    }
}
