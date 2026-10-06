package com.example.enterprise.file.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * 文件存储抽象： 本地 / MinIO / OSS 可替换实现
 */
public interface FileStorage {

    /**
     * 保存文件，返回相对路径
     *
     * @param file        上传文件
     * @param storageName 存储文件名（已生成）
     * @return 相对路径，如 2026/10/05/uuid.png
     */
    String store(MultipartFile file, String storageName);

    /**
     * 打开文件输入流（调用方负责关闭）
     */
    InputStream open(String storagePath);

    /**
     * 解析为本地可读 Path（仅本地存储使用； 对象存储可以抛出不支持）
     */
    Path resolvePath(String storagePath);

    /**
     * 删除物理文件（幂等，不存在不抛错）
     */
    void delete(String storagePath);

}
