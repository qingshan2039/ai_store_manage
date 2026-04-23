package com.aistore.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 自动填充处理器
 * 自动填充审计字段：createdAt、updatedAt
 */
@Component
public class MyBatisMetaObjectHandler implements MetaObjectHandler {

    private static final Logger log = LoggerFactory.getLogger(MyBatisMetaObjectHandler.class);

    /**
     * 插入时自动填充 createdAt 和 updatedAt
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.debug("自动填充插入时间字段");
        LocalDateTime now = LocalDateTime.now();
        this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
        this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
    }

    /**
     * 更新时自动填充 updatedAt
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.debug("自动填充更新时间字段");
        this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
    }
}
