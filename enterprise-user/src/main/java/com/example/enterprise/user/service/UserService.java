package com.example.enterprise.user.service;

import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.user.dto.UserCreateDTO;
import com.example.enterprise.user.dto.UserPasswordDTO;
import com.example.enterprise.user.dto.UserQueryDTO;
import com.example.enterprise.user.dto.UserStatusDTO;
import com.example.enterprise.user.dto.UserUpdateDTO;
import com.example.enterprise.user.vo.UserVO;

public interface UserService {

    PageResult<UserVO> page(UserQueryDTO query);

    UserVO getById(Long id);

    Long create(UserCreateDTO dto);

    void update(Long id, UserUpdateDTO dto);

    void delete(Long id);

    void updateStatus(Long id, UserStatusDTO dto);

    void updatePassword(Long id, UserPasswordDTO dto);
}

