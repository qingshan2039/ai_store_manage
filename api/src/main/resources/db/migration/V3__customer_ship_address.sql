-- ============================================================
-- Flyway V3：顾客送货地址改造（PostgreSQL）
-- 同一连锁客户可有多个收/发货地址：单值 ship_address 改为一对多子表，并新增 remark。
-- ============================================================
CREATE TABLE IF NOT EXISTS customer_ship_address (
    id           BIGSERIAL    PRIMARY KEY,
    customer_id  BIGINT       NOT NULL,
    address      VARCHAR(255) NOT NULL,
    remark       VARCHAR(255),
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_csa_customer_id ON customer_ship_address (customer_id);

COMMENT ON TABLE  customer_ship_address        IS '顾客送货地址（一个客户可有多个收/发货地址）';
COMMENT ON COLUMN customer_ship_address.remark IS '送货地址备注（如客户报错地址后填写的修正说明）';

-- 将 customer 上已有的单一 ship_address 迁移为子表的一条记录
INSERT INTO customer_ship_address (customer_id, address)
SELECT id, ship_address FROM customer WHERE ship_address IS NOT NULL;

-- 移除 customer 上的单一 ship_address 列
ALTER TABLE customer DROP COLUMN IF EXISTS ship_address;
