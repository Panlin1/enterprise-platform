package com.example.enterprise.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.redis.RedisConstants;
import com.example.enterprise.common.redis.RedisKeyBuilder;
import com.example.enterprise.common.redis.RedisUtils;
import com.example.enterprise.system.entity.SysDict;
import com.example.enterprise.system.entity.SysDictItem;
import com.example.enterprise.system.mapper.SysDictItemMapper;
import com.example.enterprise.system.mapper.SysDictMapper;
import com.example.enterprise.system.service.DictService;
import com.example.enterprise.system.vo.DictItemVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DictServiceImpl implements DictService {

    private final SysDictMapper dictMapper;
    private final SysDictItemMapper dictItemMapper;
    private final RedisUtils redisUtils;

    public DictServiceImpl(SysDictMapper dictMapper, SysDictItemMapper dictItemMapper, RedisUtils redisUtils) {
        this.dictMapper = dictMapper;
        this.dictItemMapper = dictItemMapper;
        this.redisUtils = redisUtils;
    }

    @Override
    public List<SysDict> listTypes() {
        return dictMapper.selectList(new LambdaQueryWrapper<SysDict>().orderByAsc(SysDict::getId));
    }

    @Override
    public List<DictItemVO> listItems(String dictType) {
        String key = RedisKeyBuilder.systemDict(dictType);
        List<DictItemVO> cached = redisUtils.get(key);
        if (cached != null) {
            return cached;
        }
        List<SysDictItem> items = dictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                .eq(SysDictItem::getDictType, dictType)
                .eq(SysDictItem::getStatus, 1)
                .orderByAsc(SysDictItem::getSort));
        List<DictItemVO> vos = items.stream().map(this::toItemVO).collect(Collectors.toList());
        redisUtils.setWithJitter(key, vos, RedisConstants.DICT_TTL);
        return vos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createType(SysDict dict) {
        Long c = dictMapper.selectCount(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictType, dict.getDictType()));
        if (c != null && c > 0) {
            throw BusinessException.of(ErrorCode.CONFLICT, "字典类型已存在");
        }
        if (dict.getStatus() == null) {
            dict.setStatus(1);
        }
        dictMapper.insert(dict);
        return dict.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateType(SysDict dict) {
        dictMapper.updateById(dict);
        if (dict.getDictType() != null) {
            redisUtils.evict(RedisKeyBuilder.systemDict(dict.getDictType()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteType(Long id) {
        SysDict dict = dictMapper.selectById(id);
        if (dict == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "字典不存在");
        }
        dictMapper.deleteById(id);
        dictItemMapper.delete(new LambdaQueryWrapper<SysDictItem>().eq(SysDictItem::getDictType, dict.getDictType()));
        redisUtils.evict(RedisKeyBuilder.systemDict(dict.getDictType()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createItem(SysDictItem item) {
        if (item.getSort() == null) {
            item.setSort(0);
        }
        if (item.getStatus() == null) {
            item.setStatus(1);
        }
        dictItemMapper.insert(item);
        redisUtils.evict(RedisKeyBuilder.systemDict(item.getDictType()));
        return item.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(SysDictItem item) {
        dictItemMapper.updateById(item);
        SysDictItem db = dictItemMapper.selectById(item.getId());
        if (db != null) {
            redisUtils.evict(RedisKeyBuilder.systemDict(db.getDictType()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteItem(Long id) {
        SysDictItem item = dictItemMapper.selectById(id);
        if (item != null) {
            dictItemMapper.deleteById(id);
            redisUtils.evict(RedisKeyBuilder.systemDict(item.getDictType()));
        }
    }

    private DictItemVO toItemVO(SysDictItem item) {
        DictItemVO vo = new DictItemVO();
        vo.setId(item.getId());
        vo.setDictType(item.getDictType());
        vo.setItemLabel(item.getItemLabel());
        vo.setItemValue(item.getItemValue());
        vo.setSort(item.getSort());
        vo.setStatus(item.getStatus());
        return vo;
    }
}

