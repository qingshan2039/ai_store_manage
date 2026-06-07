-- ============================================================
-- Flyway V4：WMS 主数据 —— 供应商 / 仓库 / 库区 / 托盘类型（PostgreSQL）
-- 仅建表，测试数据通过接口灌入。
-- ============================================================

-- 供应商
CREATE TABLE IF NOT EXISTS supplier (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(32)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    address     VARCHAR(255) NOT NULL,
    contact     VARCHAR(64),
    phone       VARCHAR(32),
    email       VARCHAR(128),
    remark      VARCHAR(500),
    status      SMALLINT     NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_supplier_code_deleted UNIQUE (code, deleted),
    CONSTRAINT uk_supplier_name_deleted UNIQUE (name, deleted)
);
CREATE INDEX IF NOT EXISTS idx_supplier_status ON supplier (status);
COMMENT ON TABLE supplier IS '供应商表';

-- 仓库
CREATE TABLE IF NOT EXISTS warehouse (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(32)  NOT NULL,
    name        VARCHAR(128) NOT NULL,
    type        VARCHAR(16)  NOT NULL,
    remark      VARCHAR(500),
    status      SMALLINT     NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_warehouse_code_deleted UNIQUE (code, deleted),
    CONSTRAINT uk_warehouse_name_deleted UNIQUE (name, deleted)
);
CREATE INDEX IF NOT EXISTS idx_warehouse_status ON warehouse (status);
COMMENT ON TABLE  warehouse      IS '仓库表';
COMMENT ON COLUMN warehouse.type IS '仓库类型：RAW/SEMI/FINISHED';

-- 库区（隶属仓库；code 在同一仓库内唯一）
CREATE TABLE IF NOT EXISTS zone (
    id            BIGSERIAL    PRIMARY KEY,
    warehouse_id  BIGINT       NOT NULL,
    code          VARCHAR(32)  NOT NULL,
    name          VARCHAR(64)  NOT NULL,
    type          VARCHAR(32),
    remark        VARCHAR(500),
    status        SMALLINT     NOT NULL DEFAULT 1,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    deleted       SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_zone_wh_code_deleted UNIQUE (warehouse_id, code, deleted)
);
CREATE INDEX IF NOT EXISTS idx_zone_warehouse_id ON zone (warehouse_id);
CREATE INDEX IF NOT EXISTS idx_zone_status ON zone (status);
COMMENT ON TABLE zone IS '库区表（隶属仓库）';

-- 托盘类型（ISO 规格）
CREATE TABLE IF NOT EXISTS pallet_type (
    id           BIGSERIAL     PRIMARY KEY,
    code         VARCHAR(32)   NOT NULL,
    name         VARCHAR(64)   NOT NULL,
    length       NUMERIC(10,2),
    width        NUMERIC(10,2),
    tare_weight  NUMERIC(10,2),
    max_load     NUMERIC(10,2),
    max_stack    INTEGER,
    remark       VARCHAR(500),
    status       SMALLINT      NOT NULL DEFAULT 1,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT,
    updated_by   BIGINT,
    deleted      SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_pallet_type_code_deleted UNIQUE (code, deleted),
    CONSTRAINT uk_pallet_type_name_deleted UNIQUE (name, deleted)
);
CREATE INDEX IF NOT EXISTS idx_pallet_type_status ON pallet_type (status);
COMMENT ON TABLE pallet_type IS '托盘类型表（ISO 规格）';
