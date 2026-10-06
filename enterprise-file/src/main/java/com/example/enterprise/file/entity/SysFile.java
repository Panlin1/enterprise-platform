package com.example.enterprise.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.enterprise.common.core.entity.BaseEntity;

/**
 * 文件元数据实体，对应表 sys_file。
 */
@TableName("sys_file")
public class SysFile extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 原始文件名
     */
    private String originalName;
    /**
     * 存储侧文件名（UUID）
     */
    private String storageName;
    /**
     * 相对存储路径
     */
    private String storagePath;
    /**
     * MIME 类型
     */
    private String contentType;
    /**
     * 文件大小（字节）
     */
    private Long fileSize;
    /**
     * 存储类型：LOCAL / MINIO / OSS
     */
    private String storageType;
    /**
     * 业务分类（可选）
     */
    private String bizType;
    /**
     * 上传人用户 ID
     */
    private Long uploadedBy;
    /**
     * 状态：1 正常 0 禁用
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public String getStorageName() {
        return storageName;
    }

    public void setStorageName(String storageName) {
        this.storageName = storageName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStorageType() {
        return storageType;
    }

    public void setStorageType(String storageType) {
        this.storageType = storageType;
    }

    public String getBizType() {
        return bizType;
    }

    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    public Long getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(Long uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
