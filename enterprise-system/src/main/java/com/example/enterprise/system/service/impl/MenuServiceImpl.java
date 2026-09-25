package com.example.enterprise.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.system.entity.SysMenu;
import com.example.enterprise.system.mapper.SysMenuMapper;
import com.example.enterprise.system.service.MenuService;
import com.example.enterprise.system.vo.MenuVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MenuServiceImpl implements MenuService {

    private final SysMenuMapper menuMapper;

    public MenuServiceImpl(SysMenuMapper menuMapper) {
        this.menuMapper = menuMapper;
    }

    @Override
    public List<MenuVO> tree() {
        return buildTree(listAll());
    }

    @Override
    public List<MenuVO> listAll() {
        List<SysMenu> menus = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .orderByAsc(SysMenu::getSort)
                .orderByAsc(SysMenu::getId));
        return menus.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(0L);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        menuMapper.insert(menu);
        return menu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysMenu menu) {
        if (menu.getId() == null) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "菜单 ID 不能为空");
        }
        if (menuMapper.selectById(menu.getId()) == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "菜单不存在");
        }
        menuMapper.updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Long childCount = menuMapper.selectCount(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, id));
        if (childCount != null && childCount > 0) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "请先删除子菜单");
        }
        menuMapper.deleteById(id);
    }

    private List<MenuVO> buildTree(List<MenuVO> flat) {
        Map<Long, MenuVO> map = flat.stream().collect(Collectors.toMap(MenuVO::getId, m -> m, (a, b) -> a));
        List<MenuVO> roots = new ArrayList<>();
        for (MenuVO node : flat) {
            Long pid = node.getParentId() == null ? 0L : node.getParentId();
            if (pid == 0L || !map.containsKey(pid)) {
                roots.add(node);
            } else {
                map.get(pid).getChildren().add(node);
            }
        }
        roots.sort(Comparator.comparing(MenuVO::getSort, Comparator.nullsLast(Integer::compareTo)));
        return roots;
    }

    private MenuVO toVO(SysMenu m) {
        MenuVO vo = new MenuVO();
        vo.setId(m.getId());
        vo.setParentId(m.getParentId());
        vo.setMenuName(m.getMenuName());
        vo.setMenuType(m.getMenuType());
        vo.setPath(m.getPath());
        vo.setComponent(m.getComponent());
        vo.setPermissionCode(m.getPermissionCode());
        vo.setIcon(m.getIcon());
        vo.setSort(m.getSort());
        vo.setVisible(m.getVisible());
        vo.setStatus(m.getStatus());
        return vo;
    }
}

