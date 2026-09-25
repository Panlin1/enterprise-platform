package com.example.enterprise.system.service;

import com.example.enterprise.system.entity.SysMenu;
import com.example.enterprise.system.vo.MenuVO;

import java.util.List;

public interface MenuService {

    List<MenuVO> tree();

    List<MenuVO> listAll();

    Long create(SysMenu menu);

    void update(SysMenu menu);

    void delete(Long id);
}

