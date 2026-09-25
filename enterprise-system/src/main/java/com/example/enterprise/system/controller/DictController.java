package com.example.enterprise.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;

import com.example.enterprise.common.core.result.Result;
import com.example.enterprise.system.entity.SysDict;
import com.example.enterprise.system.entity.SysDictItem;
import com.example.enterprise.system.service.DictService;
import com.example.enterprise.system.vo.DictItemVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "字典管理")
@RestController
@RequestMapping("/api/system/dicts")
public class DictController {

    private final DictService dictService;

    public DictController(DictService dictService) {
        this.dictService = dictService;
    }

    @Operation(summary = "字典类型列表")
    @GetMapping
    @SaCheckPermission("system:dict:list")
    public Result<List<SysDict>> listTypes() {
        return Result.success(dictService.listTypes());
    }

    @Operation(summary = "按类型查字典项（带缓存）")
    @GetMapping("/{dictType}/items")
    @SaCheckPermission("system:dict:list")
    public Result<List<DictItemVO>> listItems(@PathVariable String dictType) {
        return Result.success(dictService.listItems(dictType));
    }

    @Operation(summary = "新增字典类型")
    @PostMapping
    @SaCheckPermission("system:dict:list")
    public Result<Long> createType(@RequestBody SysDict dict) {
        return Result.success(dictService.createType(dict));
    }

    @Operation(summary = "修改字典类型")
    @PutMapping("/{id}")
    @SaCheckPermission("system:dict:list")
    public Result<Void> updateType(@PathVariable Long id, @RequestBody SysDict dict) {
        dict.setId(id);
        dictService.updateType(dict);
        return Result.success();
    }

    @Operation(summary = "删除字典类型")
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:dict:list")
    public Result<Void> deleteType(@PathVariable Long id) {
        dictService.deleteType(id);
        return Result.success();
    }

    @Operation(summary = "新增字典项")
    @PostMapping("/items")
    @SaCheckPermission("system:dict:list")
    public Result<Long> createItem(@RequestBody SysDictItem item) {
        return Result.success(dictService.createItem(item));
    }

    @Operation(summary = "修改字典项")
    @PutMapping("/items/{id}")
    @SaCheckPermission("system:dict:list")
    public Result<Void> updateItem(@PathVariable Long id, @RequestBody SysDictItem item) {
        item.setId(id);
        dictService.updateItem(item);
        return Result.success();
    }

    @Operation(summary = "删除字典项")
    @DeleteMapping("/items/{id}")
    @SaCheckPermission("system:dict:list")
    public Result<Void> deleteItem(@PathVariable Long id) {
        dictService.deleteItem(id);
        return Result.success();
    }
}

