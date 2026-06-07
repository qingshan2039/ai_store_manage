-- ============================================================
-- V7：库存与托盘（Phase C）
--   location   仓库内库位（隶属仓库/库区）
--   lpn        托盘实例（SSCC，状态 在库/在途/空置）
--   inventory  库存（基本单位记账，挂托盘 lpn_id / 库位 location_id）
-- 需求②的"库存数量/托盘数量/整托尾托"由 inventory + lpn 统计得出。
-- ============================================================

CREATE TABLE IF NOT EXISTS location (
    id           BIGSERIAL    PRIMARY KEY,
    warehouse_id BIGINT       NOT NULL,
    zone_id      BIGINT,
    code         VARCHAR(32)  NOT NULL,
    loc_type     VARCHAR(32),
    status       SMALLINT     NOT NULL DEFAULT 1,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT,
    updated_by   BIGINT,
    deleted      SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_location_wh_code_deleted UNIQUE (warehouse_id, code, deleted)
);
CREATE INDEX IF NOT EXISTS idx_location_warehouse ON location (warehouse_id);
CREATE INDEX IF NOT EXISTS idx_location_zone ON location (zone_id);
COMMENT ON TABLE location IS '库位（隶属仓库/库区）';

CREATE TABLE IF NOT EXISTS lpn (
    id             BIGSERIAL    PRIMARY KEY,
    lpn_code       VARCHAR(64)  NOT NULL,
    pallet_type_id BIGINT       NOT NULL,
    warehouse_id   BIGINT       NOT NULL,
    location_id    BIGINT,
    status         VARCHAR(16)  NOT NULL DEFAULT 'IN_STOCK',
    gross_weight   NUMERIC(12,2),
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    deleted        SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_lpn_code_deleted UNIQUE (lpn_code, deleted)
);
CREATE INDEX IF NOT EXISTS idx_lpn_warehouse ON lpn (warehouse_id);
CREATE INDEX IF NOT EXISTS idx_lpn_location ON lpn (location_id);
COMMENT ON TABLE lpn IS '托盘实例（SSCC）';

CREATE TABLE IF NOT EXISTS inventory (
    id           BIGSERIAL     PRIMARY KEY,
    sku_id       BIGINT        NOT NULL,
    lpn_id       BIGINT,
    location_id  BIGINT,
    lot_no       VARCHAR(64),
    mfg_date     DATE,
    exp_date     DATE,
    qty_on_hand  NUMERIC(18,3) NOT NULL DEFAULT 0,
    qty_reserved NUMERIC(18,3) NOT NULL DEFAULT 0,
    created_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT,
    updated_by   BIGINT,
    deleted      SMALLINT      NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_inventory_sku ON inventory (sku_id);
CREATE INDEX IF NOT EXISTS idx_inventory_lpn ON inventory (lpn_id);
CREATE INDEX IF NOT EXISTS idx_inventory_location ON inventory (location_id);
COMMENT ON TABLE inventory IS '库存（基本单位记账）';
