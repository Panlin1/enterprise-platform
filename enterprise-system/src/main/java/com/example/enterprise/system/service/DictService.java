package com.example.enterprise.system.service;

import com.example.enterprise.system.entity.SysDict;
import com.example.enterprise.system.entity.SysDictItem;
import com.example.enterprise.system.vo.DictItemVO;

import java.util.List;

public interface DictService {

    List<SysDict> listTypes();

    List<DictItemVO> listItems(String dictType);

    Long createType(SysDict dict);

    void updateType(SysDict dict);

    void deleteType(Long id);

    Long createItem(SysDictItem item);

    void updateItem(SysDictItem item);

    void deleteItem(Long id);
}
