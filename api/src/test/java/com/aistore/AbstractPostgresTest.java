package com.aistore;

import org.springframework.test.context.ActiveProfiles;

/**
 * 集成测试基类。
 *
 * 测试连接 application-test.yml 指定的 PostgreSQL 测试库（dev 容器 ai-store-pg 内的 ai_store_test，
 * 与 dev 业务库隔离），由 Flyway 自动迁移；各测试类以 @Transactional 在事务中执行并回滚，互不影响。
 *
 * 前置条件：本机 dev PostgreSQL 容器（ai-store-pg，端口 5433）运行中，且已创建 ai_store_test 库。
 * 详见 docs/design/testing.md。
 *
 * 说明：本机 Docker Engine 版本要求的最小 API ≥ 1.40，而当前 Testcontainers 自带的 docker-java
 * 固定使用 API 1.32 被拒，故集成测试直接连这台真实 PG，而非由 Testcontainers 临时拉起容器。
 */
@ActiveProfiles("test")
public abstract class AbstractPostgresTest {
}
