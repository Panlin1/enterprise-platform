package com.example.enterprise.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.enterprise.file.entity.SysFile;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件元数据 Mapper
 */
@Mapper
public interface SysFileMapper extends BaseMapper<SysFile> {
}
