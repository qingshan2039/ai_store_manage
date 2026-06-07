package com.aistore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 上下文加载测试：验证 Spring ApplicationContext 在真实 PostgreSQL（测试库 ai_store_test）上
 * 正常启动，且 Flyway 迁移成功执行。
 */
@SpringBootTest
class AiStoreManageApplicationTests extends AbstractPostgresTest {

    @Test
    void contextLoads() {
        // 能加载即说明数据源、Flyway 迁移、MyBatis-Plus、各模块 Bean 装配均正常
    }
}
