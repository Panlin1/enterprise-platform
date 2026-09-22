package com.example.enterprise.common.mybatis;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.example.enterprise.common.core.constant.CommonConstants;
import com.example.enterprise.common.core.context.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus auto-fill for {@link com.example.enterprise.common.core.entity.BaseEntity} fields.
 * Registered when MyBatis-Plus is on the classpath.
 */
@Component
@ConditionalOnClass(MetaObjectHandler.class)
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MybatisPlusMetaObjectHandler.class);

    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = safeUserId();

        strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
        strictInsertFill(metaObject, "createdBy", Long.class, userId);
        strictInsertFill(metaObject, "updatedBy", Long.class, userId);
        strictInsertFill(metaObject, "deleted", Integer.class, CommonConstants.DELETED_NO);

        if (log.isDebugEnabled()) {
            log.debug("insertFill userId={}", userId);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        Long userId = safeUserId();

        strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, now);
        strictUpdateFill(metaObject, "updatedBy", Long.class, userId);

        if (log.isDebugEnabled()) {
            log.debug("updateFill userId={}", userId);
        }
    }

    private Long safeUserId() {
        try {
            return UserContext.getUserId();
        } catch (Exception e) {
            return null;
        }
    }
}
