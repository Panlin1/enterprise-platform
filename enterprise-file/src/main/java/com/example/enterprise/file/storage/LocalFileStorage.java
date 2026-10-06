package com.example.enterprise.file.storage;

import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 本地磁盘存储实现。
 * <p>
 * 根目录由配置 file.storage.local-base-dir 指定。
 */
@Component
public class LocalFileStorage implements FileStorage {

    private static final Logger log = LoggerFactory.getLogger(LocalFileStorage.class);
    private static final DateTimeFormatter DAY = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Value("${file.storage.local-base-dir:./data/files}")
    private String baseDir;

    @Override
    public String store(MultipartFile file, String storageName) {
        String relativeDir = LocalDate.now().format(DAY);
        Path dir = Paths.get(baseDir, relativeDir);
        try {
            Files.createDirectories(dir);
            Path target = dir.resolve(storageName);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            String relative = relativeDir + "/" + storageName;
            log.info("文件已存储 path={}", relative);
            return relative.replace("\\", "/");
        } catch (IOException e) {
            log.error("文件存储失败", e);
            throw BusinessException.of(ErrorCode.INTERNAL_ERROR, "文件存储失败");
        }
    }

    @Override
    public InputStream open(String storagePath) {
        try {
            return Files.newInputStream(resolvePath(storagePath));
        } catch (IOException e) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "文件不存在或无法读取");
        }
    }

    @Override
    public Path resolvePath(String storagePath) {
        Path path = Paths.get(baseDir, storagePath).normalize();
        Path root = Paths.get(baseDir).toAbsolutePath().normalize();
        // 防止路径穿越
        if (!path.toAbsolutePath().normalize().startsWith(root)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "非法文件路径");
        }
        return path;
    }

    @Override
    public void delete(String storagePath) {
        try {
            Files.deleteIfExists(resolvePath(storagePath));
        } catch (IOException e) {
            log.warn("删除物理文件失败 path={} err={}", storagePath, e.getMessage());
        }
    }

    /**
     * 存储根目录（绝对路径）。
     */
    public Path getRootDir() {
        return Paths.get(baseDir).toAbsolutePath().normalize();
    }
}
