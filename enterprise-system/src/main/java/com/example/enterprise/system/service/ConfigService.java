package com.example.enterprise.system.service;

import com.example.enterprise.common.core.query.PageQuery;
import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.system.entity.SysConfig;
import com.example.enterprise.system.vo.ConfigVO;

public interface ConfigService {

    PageResult<ConfigVO> page(PageQuery query, String configKey);

    String getValue(String configKey);

    Long create(SysConfig config);

    void update(SysConfig config);

    void delete(Long id);
}

