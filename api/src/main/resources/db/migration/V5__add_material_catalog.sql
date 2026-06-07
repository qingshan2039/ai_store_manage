-- ============================================================
-- V5：物料目录主数据（品类 / SPU / SKU）
-- 原材料管理 Phase A：建立"有哪些料、什么规格"的目录层。
--   material_category 品类（纸皮/铝箔/纸管/保鲜膜/烘焙纸…，含种子）
--   spu              标准产品单元（品类层，挂 category_code、base_unit）
--   sku              最小库存单元（item_type、结构化尺寸、jsonb 规格）
-- ============================================================

-- ── 物料品类 ──
CREATE TABLE IF NOT EXISTS material_category (
    id          BIGSERIAL    PRIMARY KEY,
    code        VARCHAR(32)  NOT NULL,
    name        VARCHAR(64)  NOT NULL,
    sort_order  INTEGER      NOT NULL DEFAULT 0,
    status      SMALLINT     NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_material_category_code_deleted UNIQUE (code, deleted),
    CONSTRAINT uk_material_category_name_deleted UNIQUE (name, deleted)
);
COMMENT ON TABLE material_category IS '物料品类表';

-- ── SPU 标准产品单元 ──
CREATE TABLE IF NOT EXISTS spu (
    id            BIGSERIAL     PRIMARY KEY,
    spu_code      VARCHAR(32)   NOT NULL,
    spu_name      VARCHAR(128)  NOT NULL,
    category_code VARCHAR(32)   NOT NULL,
    brand         VARCHAR(64),
    base_unit     VARCHAR(16)   NOT NULL,
    status        SMALLINT      NOT NULL DEFAULT 1,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    deleted       SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_spu_code_deleted UNIQUE (spu_code, deleted),
    CONSTRAINT uk_spu_name_deleted UNIQUE (spu_name, deleted)
);
CREATE INDEX IF NOT EXISTS idx_spu_category ON spu (category_code);
COMMENT ON TABLE spu IS '标准产品单元（物料主，品类层）';

-- ── SKU 最小库存单元 ──
CREATE TABLE IF NOT EXISTS sku (
    id            BIGSERIAL     PRIMARY KEY,
    spu_id        BIGINT        NOT NULL,
    sku_code      VARCHAR(48)   NOT NULL,
    sku_name      VARCHAR(128)  NOT NULL,
    item_type     VARCHAR(16)   NOT NULL,
    length_mm     NUMERIC(12,2),
    width_mm      NUMERIC(12,2),
    thickness_mm  NUMERIC(12,2),
    roll_length_m NUMERIC(12,2),
    color         VARCHAR(32),
    gsm           NUMERIC(10,2),
    spec          JSONB,
    status        SMALLINT      NOT NULL DEFAULT 1,
    created_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by    BIGINT,
    updated_by    BIGINT,
    deleted       SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_sku_code_deleted UNIQUE (sku_code, deleted)
);
CREATE INDEX IF NOT EXISTS idx_sku_spu ON sku (spu_id);
CREATE INDEX IF NOT EXISTS idx_sku_item_type ON sku (item_type);
COMMENT ON TABLE sku IS '最小库存单元（含 item_type、结构化尺寸、jsonb 规格）';

-- ── 品类种子（5 类原材料） ──
INSERT INTO material_category (code, name, sort_order) VALUES
    ('PAPER',  '纸皮',   1),
    ('FOIL',   '铝箔',   2),
    ('CORE',   '纸管',   3),
    ('FILM',   '保鲜膜', 4),
    ('BAKING', '烘焙纸', 5)
ON CONFLICT (code, deleted) DO NOTHING;
