package com.example.enterprise.file.task;

import com.example.enterprise.file.entity.SysFile;
import com.example.enterprise.file.mapper.SysFileMapper;
import com.example.enterprise.file.storage.LocalFileStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 孤儿文件清理：磁盘文件 ↔ sys_file 表对比。
 * 仅清理「落盘超过 24h 且 DB 无有效引用」的文件：
 * - 覆盖上传事务回滚/进程崩溃遗留的孤儿
 * - 覆盖 delete 物理删除失败残留（deleted=1 行不在 selectList 结果中 → 磁盘残留即孤儿）
 * - 24h 时间窗规避「落盘完成、事务未提交」的并发竞态误删
 * 启动执行一次；如需每日定时，启动类加 @EnableScheduling 并在 clean() 上注 @Scheduled。
 */
@Component
@Slf4j
public class OrphanFileCleaner {

    /**
     * 判定孤儿的时间窗：落盘超过该时长且无引用的文件才清理
     */
    private static final long ORPHAN_AGE_MILLIS = 24L * 3600 * 1000;

    private final SysFileMapper fileMapper;
    private final LocalFileStorage localFileStorage;

    public OrphanFileCleaner(SysFileMapper fileMapper, LocalFileStorage localFileStorage) {
        this.fileMapper = fileMapper;
        this.localFileStorage = localFileStorage;
    }

    /**
     * 启动完成后执行一次
     */
    @EventListener(ApplicationReadyEvent.class)
    public void cleanOnStartup() {
        clean();
    }

    public void clean() {
        // 1. DB 有效引用集合（selectList 自动 WHERE deleted=0，
        //    deleted=1 的残留文件天然不在集合内 → 磁盘残留即孤儿）
        Set<String> referenced = fileMapper.selectList(null).stream()
                .map(SysFile::getStorageName)
                .collect(Collectors.toSet());

        // 2. 扫描磁盘根目录（LocalFileStorage 需新增 getRootDir()）
        Path root = localFileStorage.getRootDir();
        if (!Files.exists(root)) {
            return;
        }

        long cutoff = System.currentTimeMillis() - ORPHAN_AGE_MILLIS;
        List<Path> orphans = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(root)) {
            walk.filter(Files::isRegularFile)
                    .filter(p -> isOlderThan(p, cutoff))          // 规避并发竞态
                    .forEach(p -> {
                        String name = p.getFileName().toString();
                        if (!referenced.contains(name)) {
                            orphans.add(p);
                        }
                    });
        } catch (IOException e) {
            log.error("扫描文件目录失败", e);
            return;
        }

        // 3. 删除孤儿
        for (Path p : orphans) {
            try {
                Files.deleteIfExists(p);
                log.info("清理孤儿文件 path={}", p);
            } catch (IOException e) {
                log.warn("清理孤儿文件失败 path={} err={}", p, e.getMessage());
            }
        }
    }

    /**
     * 文件最后修改时间是否早于 cutoff
     */
    private boolean isOlderThan(Path p, long cutoff) {
        try {
            return Files.getLastModifiedTime(p).toMillis() < cutoff;
        } catch (IOException e) {
            log.debug("读取文件时间失败，跳过 path={}", p);
            return false; // 读不到时间就不清理，保守起见
        }
    }
}
