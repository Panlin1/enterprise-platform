package com.example.enterprise.file.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.file.entity.SysFile;
import com.example.enterprise.file.mapper.SysFileMapper;
import com.example.enterprise.file.service.FileService;
import com.example.enterprise.file.storage.FileStorage;
import com.example.enterprise.file.vo.FileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * 文件业务实现：校验 → 落盘 → 写元数据。
 */
@Service
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);

    private final SysFileMapper fileMapper;
    private final FileStorage fileStorage;

    /**
     * 单文件大小上限（字节）
     */
    @Value("${file.upload.max-size:10485760}")
    private long maxSize;

    /**
     * 允许的后缀，逗号分隔，小写
     */
    @Value("${file.upload.allowed-extensions:jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,txt}")
    private String allowedExtensions;

    public FileServiceImpl(SysFileMapper fileMapper, FileStorage fileStorage) {
        this.fileMapper = fileMapper;
        this.fileStorage = fileStorage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO upload(MultipartFile file, String bizType) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "上传文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "文件超过大小限制");
        }
        String original = file.getOriginalFilename();
        if (!StringUtils.hasText(original)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "文件名无效");
        }
        String ext = extractExt(original);
        if (!isAllowed(ext)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "不支持的文件类型: " + ext);
        }

        String storageName = UUID.randomUUID().toString().replace("-", "")
                + (ext.isEmpty() ? "" : "." + ext);
        // ① 落盘
        String storagePath = fileStorage.store(file, storageName);
        // ② 注册补偿 ← 新增
        registerRollbackCleanup(storagePath);

        SysFile entity = new SysFile();
        entity.setOriginalName(original);
        entity.setStorageName(storageName);
        entity.setStoragePath(storagePath);
        entity.setContentType(file.getContentType());
        entity.setFileSize(file.getSize());
        entity.setStorageType("LOCAL");
        entity.setBizType(bizType);
        entity.setStatus(1);
        if (StpUtil.isLogin()) {
            entity.setUploadedBy(StpUtil.getLoginIdAsLong());
        }
        // ③ 入库
        fileMapper.insert(entity);
        log.info("文件上传成功 id={} name={}", entity.getId(), original);
        return toVO(entity);
    }

    private void registerRollbackCleanup(String storagePath) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            // 非事务上下文，无需补偿
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == TransactionSynchronization.STATUS_ROLLED_BACK) {
                    fileStorage.delete(storagePath);
                    log.warn("事务回滚，已补偿删除落盘文件 path={}", storagePath);
                }
            }
        });
    }

    @Override
    public FileVO getById(Long id) {
        return toVO(requireFile(id));
    }

    @Override
    public Resource loadAsResource(Long id) {
        SysFile file = requireFile(id);
        Resource resource = new FileSystemResource(fileStorage.resolvePath(file.getStoragePath()));
        if (!resource.exists() || !resource.isReadable()) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "文件内容不存在");
        }
        return resource;
    }

    @Override
    public String getOriginalName(Long id) {
        return requireFile(id).getOriginalName();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        SysFile file = requireFile(id);
        fileMapper.deleteById(id);
        fileStorage.delete(file.getStoragePath());
        log.info("文件已删除 id={}", id);
    }

    private SysFile requireFile(Long id) {
        SysFile file = fileMapper.selectById(id);
        if (file == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "文件不存在");
        }
        return file;
    }

    private String extractExt(String name) {
        int i = name.lastIndexOf('.');
        if (i < 0 || i == name.length() - 1) {
            return "";
        }
        return name.substring(i + 1).toLowerCase(Locale.ROOT);
    }

    private boolean isAllowed(String ext) {
        if (!StringUtils.hasText(ext)) {
            return false;
        }
        Set<String> set = Set.of(allowedExtensions.toLowerCase(Locale.ROOT).split(","));
        return set.contains(ext.trim());
    }

    private FileVO toVO(SysFile e) {
        FileVO vo = new FileVO();
        vo.setId(e.getId());
        vo.setOriginalName(e.getOriginalName());
        vo.setStorageName(e.getStorageName());
        vo.setContentType(e.getContentType());
        vo.setFileSize(e.getFileSize());
        vo.setStorageType(e.getStorageType());
        vo.setBizType(e.getBizType());
        vo.setUploadedBy(e.getUploadedBy());
        vo.setCreatedAt(e.getCreatedAt());
        return vo;
    }
}

