package com.aistore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AiStoreManageApplicationTests {

    @Test
    void contextLoads() {
        // 验证 Spring ApplicationContext 可以正常启动
        // 使用 application-test.yml 中的 H2 内存数据库，无需连接 MySQL
    }

}
