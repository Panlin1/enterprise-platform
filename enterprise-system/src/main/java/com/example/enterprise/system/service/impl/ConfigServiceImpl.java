package com.example.enterprise.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.query.PageQuery;
import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.system.entity.SysConfig;
import com.example.enterprise.system.mapper.SysConfigMapper;
import com.example.enterprise.system.service.ConfigService;
import com.example.enterprise.system.vo.ConfigVO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfigServiceImpl implements ConfigService {

    private final SysConfigMapper configMapper;

    public ConfigServiceImpl(SysConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    @Override
    public PageResult<ConfigVO> page(PageQuery query, String configKey) {
        LambdaQueryWrapper<SysConfig> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(configKey), SysConfig::getConfigKey, configKey)
                .orderByAsc(SysConfig::getId);
        Page<SysConfig> page = configMapper.selectPage(query.toPage(), w);
        List<ConfigVO> list = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(page, list);
    }

    @Override
    public String getValue(String configKey) {
        SysConfig c = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, configKey)
                .last("LIMIT 1"));
        return c == null ? null : c.getConfigValue();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(SysConfig config) {
        Long cnt = configMapper.selectCount(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, config.getConfigKey()));
        if (cnt != null && cnt > 0) {
            throw BusinessException.of(ErrorCode.CONFLICT, "配置键已存在");
        }
        if (config.getConfigType() == null) {
            config.setConfigType(1);
        }
        configMapper.insert(config);
        return config.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SysConfig config) {
        configMapper.updateById(config);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        configMapper.deleteById(id);
    }

    private ConfigVO toVO(SysConfig c) {
        ConfigVO vo = new ConfigVO();
        vo.setId(c.getId());
        vo.setConfigKey(c.getConfigKey());
        vo.setConfigValue(c.getConfigValue());
        vo.setConfigName(c.getConfigName());
        vo.setConfigType(c.getConfigType());
        vo.setRemark(c.getRemark());
        return vo;
    }
}

