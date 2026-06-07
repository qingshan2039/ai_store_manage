-- ============================================================
-- Flyway V2：新增 customer（顾客 / 客户公司）表（PostgreSQL）
-- 说明：V1 已发布不可修改，顾客模块的建表作为递增迁移 V2。
-- ============================================================
CREATE TABLE IF NOT EXISTS customer (
    id            BIGSERIAL    PRIMARY KEY,
    code          VARCHAR(32)  NOT NULL,
    name          VARCHAR(128) NOT NULL,
    address       VARCHAR(255) NOT NULL,
    ship_address  VARCHAR(255) NOT NULL,
    contact       VARCHAR(64),
    phone         VARCHAR(32),
    email         VARCHAR(128),
    remark        VARCHAR(500),
    status        SMALLINT     NOT NULL DEFAULT 1,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    deleted       SMALLINT     NOT NULL DEFAULT 0,
    -- 唯一约束（含 deleted，逻辑删除后允许复用 code / name）
    CONSTRAINT uk_customer_code_deleted UNIQUE (code, deleted),
    CONSTRAINT uk_customer_name_deleted UNIQUE (name, deleted)
);

CREATE INDEX IF NOT EXISTS idx_customer_status ON customer (status);

COMMENT ON TABLE  customer              IS '顾客（客户公司）表';
COMMENT ON COLUMN customer.ship_address IS '收/发货地址（ship-to）';
COMMENT ON COLUMN customer.status       IS '状态：0=禁用，1=启用';
COMMENT ON COLUMN customer.deleted      IS '逻辑删除：0=未删除，1=已删除';
