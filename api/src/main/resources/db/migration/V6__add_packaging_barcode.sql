-- ============================================================
-- V6：包装与条码（Phase B）
--   packaging_level     SKU 多级包装（卷/箱/托）
--   packaging_relation  父层含子层数量（每托 500/480、12/16/18），is_fixed_qty 标识整托
--   barcode             包装层条码（EAN13/ITF14/SSCC）
--   unit_conversion     SKU 单位换算
--   item_image          SPU/SKU/包装层图片（URL）
-- ============================================================

CREATE TABLE IF NOT EXISTS packaging_level (
    id            BIGSERIAL     PRIMARY KEY,
    sku_id        BIGINT        NOT NULL,
    level_name    VARCHAR(32)   NOT NULL,
    level_seq     INTEGER       NOT NULL,
    unit_code     VARCHAR(16)   NOT NULL,
    length        NUMERIC(12,2),
    width         NUMERIC(12,2),
    height        NUMERIC(12,2),
    net_weight    NUMERIC(12,2),
    gross_weight  NUMERIC(12,2),
    is_base_unit  SMALLINT      NOT NULL DEFAULT 0,
    is_sellable   SMALLINT      NOT NULL DEFAULT 0,
    status        SMALLINT      NOT NULL DEFAULT 1,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    deleted       SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_packaging_level_sku_seq_deleted UNIQUE (sku_id, level_seq, deleted)
);
CREATE INDEX IF NOT EXISTS idx_packaging_level_sku ON packaging_level (sku_id);
COMMENT ON TABLE packaging_level IS 'SKU 多级包装层（卷/箱/托）';

CREATE TABLE IF NOT EXISTS packaging_relation (
    id              BIGSERIAL     PRIMARY KEY,
    parent_level_id BIGINT        NOT NULL,
    child_level_id  BIGINT        NOT NULL,
    child_qty       NUMERIC(14,3) NOT NULL,
    is_fixed_qty    SMALLINT      NOT NULL DEFAULT 1,
    tare_weight     NUMERIC(12,2),
    status          SMALLINT      NOT NULL DEFAULT 1,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    deleted         SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_packaging_relation_pc_deleted UNIQUE (parent_level_id, child_level_id, deleted)
);
CREATE INDEX IF NOT EXISTS idx_packaging_relation_parent ON packaging_relation (parent_level_id);
COMMENT ON TABLE packaging_relation IS '包装父子关系（含子层数量、是否整托）';

CREATE TABLE IF NOT EXISTS barcode (
    id           BIGSERIAL    PRIMARY KEY,
    level_id     BIGINT       NOT NULL,
    barcode      VARCHAR(64)  NOT NULL,
    barcode_type VARCHAR(16)  NOT NULL,
    is_primary   SMALLINT     NOT NULL DEFAULT 0,
    valid_from   DATE,
    valid_to     DATE,
    status       SMALLINT     NOT NULL DEFAULT 1,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by   BIGINT,
    updated_by   BIGINT,
    deleted      SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_barcode_code_deleted UNIQUE (barcode, deleted)
);
CREATE INDEX IF NOT EXISTS idx_barcode_level ON barcode (level_id);
COMMENT ON TABLE barcode IS '包装层条码';

CREATE TABLE IF NOT EXISTS unit_conversion (
    id         BIGSERIAL     PRIMARY KEY,
    sku_id     BIGINT        NOT NULL,
    from_unit  VARCHAR(16)   NOT NULL,
    to_unit    VARCHAR(16)   NOT NULL,
    factor     NUMERIC(18,6) NOT NULL,
    status     SMALLINT      NOT NULL DEFAULT 1,
    created_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted    SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_unit_conversion_sku_ft_deleted UNIQUE (sku_id, from_unit, to_unit, deleted)
);
CREATE INDEX IF NOT EXISTS idx_unit_conversion_sku ON unit_conversion (sku_id);
COMMENT ON TABLE unit_conversion IS 'SKU 单位换算';

CREATE TABLE IF NOT EXISTS item_image (
    id         BIGSERIAL    PRIMARY KEY,
    spu_id     BIGINT,
    sku_id     BIGINT,
    level_id   BIGINT,
    image_url  VARCHAR(512) NOT NULL,
    image_type VARCHAR(32),
    sort_order INTEGER      NOT NULL DEFAULT 0,
    is_primary SMALLINT     NOT NULL DEFAULT 0,
    status     SMALLINT     NOT NULL DEFAULT 1,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted    SMALLINT     NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_item_image_spu ON item_image (spu_id);
CREATE INDEX IF NOT EXISTS idx_item_image_sku ON item_image (sku_id);
CREATE INDEX IF NOT EXISTS idx_item_image_level ON item_image (level_id);
COMMENT ON TABLE item_image IS 'SPU/SKU/包装层图片（URL）';
