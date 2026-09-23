package com.example.enterprise.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprise.auth.entity.AuthUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AuthUserMapper extends BaseMapper<AuthUser> {

    @Select("""
            SELECT p.permission_code
            FROM sys_user_role ur
            INNER JOIN sys_role_permission rp ON ur.role_id = rp.role_id
            INNER JOIN sys_permission p ON rp.permission_id = p.id
            INNER JOIN sys_role r ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND p.deleted = 0 AND p.status = 1
              AND r.deleted = 0 AND r.status = 1
            """)
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT r.role_code
            FROM sys_user_role ur
            INNER JOIN sys_role r ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
              AND r.deleted = 0 AND r.status = 1
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);
}

