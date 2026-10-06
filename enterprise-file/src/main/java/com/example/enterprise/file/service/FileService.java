package com.example.enterprise.file.service;

import com.example.enterprise.file.vo.FileVO;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件业务接口
 */
public interface FileService {
    /**
     * 上传文件并保存元数据。
     *
     * @param file    文件
     * @param bizType 业务类型，可空
     * @return 文件视图
     */
    FileVO upload(MultipartFile file, String bizType);

    /**
     * 按 ID 查询元数据。
     */
    FileVO getById(Long id);

    /**
     * 下载资源（含原始文件名建议）。
     */
    Resource loadAsResource(Long id);

    /**
     * 获取下载用原始文件名。
     */
    String getOriginalName(Long id);

    /**
     * 逻辑删除元数据并删除物理文件。
     */
    void delete(Long id);
}
