package com.example.enterprise.user.converter;

import com.example.enterprise.user.dto.UserCreateDTO;
import com.example.enterprise.user.dto.UserUpdateDTO;
import com.example.enterprise.user.entity.SysUser;
import com.example.enterprise.user.vo.UserVO;

import java.util.List;

/**
 * Manual converter (MapStruct can replace later).
 */
public final class UserConverter {

    private UserConverter() {
    }

    public static SysUser toEntity(UserCreateDTO dto) {
        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setRealName(dto.getRealName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setGender(dto.getGender() == null ? 0 : dto.getGender());
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        user.setRemark(dto.getRemark());
        return user;
    }

    public static void merge(SysUser user, UserUpdateDTO dto) {
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getRealName() != null) {
            user.setRealName(dto.getRealName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }
        if (dto.getRemark() != null) {
            user.setRemark(dto.getRemark());
        }
    }

    public static UserVO toVO(SysUser user) {
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRealName(user.getRealName());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus());
        vo.setRemark(user.getRemark());
        vo.setCreatedAt(user.getCreatedAt());
        vo.setUpdatedAt(user.getUpdatedAt());
        return vo;
    }

    public static UserVO toVO(SysUser user, List<Long> roleIds, List<String> roleCodes) {
        UserVO vo = toVO(user);
        if (vo != null) {
            vo.setRoleIds(roleIds);
            vo.setRoleCodes(roleCodes);
        }
        return vo;
    }
}
