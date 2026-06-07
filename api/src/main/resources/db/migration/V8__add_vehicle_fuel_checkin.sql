-- ============================================================
-- V6：运输管理（车辆 / 打油记录 / 司机每日打卡）
--   司机/跟车员复用 sys_user（运输部=司机；仓库/生产部=跟车员）；
--   车辆配常态化司机 + 跟车员，支持 OTHER 替补（*_user_id 软引用 sys_user，或 *_other 文本替补）；
--   打油记录含小票图片 URL 数组（jsonb）；打卡按“司机 + 日期”唯一。
--   软引用（BIGINT + 索引，不建 DB 外键），与现有 sku.spu_id 一致。
--   种子：4 司机 + 4 跟车员（sys_user）、4 车（9924/6115/7744/7601）、最近 7 天打卡、少量打油。
-- ============================================================

-- ── 车辆 ──
CREATE TABLE IF NOT EXISTS vehicle (
    id                     BIGSERIAL    PRIMARY KEY,
    plate_no               VARCHAR(32)  NOT NULL,
    default_driver_user_id BIGINT,
    default_driver_other   VARCHAR(64),
    default_escort_user_id BIGINT,
    default_escort_other   VARCHAR(64),
    remark                 VARCHAR(500),
    status                 SMALLINT     NOT NULL DEFAULT 1,
    created_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by             BIGINT,
    updated_by             BIGINT,
    deleted                SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_vehicle_plate_deleted UNIQUE (plate_no, deleted)
);
CREATE INDEX IF NOT EXISTS idx_vehicle_status ON vehicle (status);
CREATE INDEX IF NOT EXISTS idx_vehicle_default_driver ON vehicle (default_driver_user_id);
COMMENT ON TABLE  vehicle IS '车辆（含常态化司机/跟车员，支持 OTHER 替补）';
COMMENT ON COLUMN vehicle.default_driver_user_id IS '常态化司机用户ID（软引用 sys_user）';
COMMENT ON COLUMN vehicle.default_driver_other   IS '常态化司机替补名（OTHER，无在册用户时填）';
COMMENT ON COLUMN vehicle.default_escort_user_id IS '常态化跟车员用户ID（软引用 sys_user）';
COMMENT ON COLUMN vehicle.default_escort_other   IS '常态化跟车员替补名（OTHER）';

-- ── 打油记录 ──
CREATE TABLE IF NOT EXISTS fuel_record (
    id             BIGSERIAL     PRIMARY KEY,
    vehicle_id     BIGINT        NOT NULL,
    driver_user_id BIGINT,
    fuel_date      DATE          NOT NULL,
    liters         NUMERIC(10,2),
    amount         NUMERIC(12,2),
    unit_price     NUMERIC(10,2),
    odometer       NUMERIC(12,1),
    images         JSONB,
    remark         VARCHAR(500),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    deleted        SMALLINT      NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_fuel_vehicle ON fuel_record (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_fuel_date ON fuel_record (fuel_date);
COMMENT ON TABLE  fuel_record IS '车辆打油（加油）流水（含小票图片 jsonb）';
COMMENT ON COLUMN fuel_record.images IS '小票/凭证图片 URL 数组（jsonb）';

-- ── 司机每日打卡 ──
CREATE TABLE IF NOT EXISTS driver_checkin (
    id             BIGSERIAL     PRIMARY KEY,
    driver_user_id BIGINT,
    driver_other   VARCHAR(64),
    vehicle_id     BIGINT,
    escort_user_id BIGINT,
    escort_other   VARCHAR(64),
    checkin_date   DATE          NOT NULL,
    clock_in_at    TIMESTAMP,
    clock_out_at   TIMESTAMP,
    checkin_status VARCHAR(16)   NOT NULL DEFAULT 'NORMAL',
    remark         VARCHAR(500),
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by     BIGINT,
    updated_by     BIGINT,
    deleted        SMALLINT      NOT NULL DEFAULT 0,
    CONSTRAINT uk_checkin_driver_date_deleted UNIQUE (driver_user_id, checkin_date, deleted)
);
CREATE INDEX IF NOT EXISTS idx_checkin_driver ON driver_checkin (driver_user_id);
CREATE INDEX IF NOT EXISTS idx_checkin_date ON driver_checkin (checkin_date);
COMMENT ON TABLE  driver_checkin IS '司机每日打卡（含当天车辆/跟车员，缺席可填替补）';
COMMENT ON COLUMN driver_checkin.checkin_status IS '出勤状态：NORMAL/LATE/ABSENT/LEAVE';

-- ============================================================
-- 种子数据
-- ============================================================

-- 1) 司机（运输部 TRANSPORT）+ 跟车员（仓库 WAREHOUSE / 生产 PRODUCTION）
--    密码均为 BCrypt 占位散列（明文 Driver@123），仅供演示登录。
INSERT INTO sys_user (employee_no, username, password, name, gender, phone_number, job_title, department_id, status) VALUES
    ('DRV-001', 'driver01', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '张建国', 1, '13900000001', '司机',
        (SELECT id FROM sys_department WHERE code='TRANS' AND deleted=0), 1),
    ('DRV-002', 'driver02', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '李志强', 1, '13900000002', '司机',
        (SELECT id FROM sys_department WHERE code='TRANS' AND deleted=0), 1),
    ('DRV-003', 'driver03', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '王海涛', 1, '13900000003', '司机',
        (SELECT id FROM sys_department WHERE code='TRANS' AND deleted=0), 1),
    ('DRV-004', 'driver04', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '赵明',   1, '13900000004', '司机',
        (SELECT id FROM sys_department WHERE code='TRANS' AND deleted=0), 1),
    ('ESC-001', 'escort01', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '陈伟',   1, '13900000005', '跟车员',
        (SELECT id FROM sys_department WHERE code='WH' AND deleted=0), 1),
    ('ESC-002', 'escort02', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '刘洋',   1, '13900000006', '跟车员',
        (SELECT id FROM sys_department WHERE code='WH' AND deleted=0), 1),
    ('ESC-003', 'escort03', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '杨光',   1, '13900000007', '跟车员',
        (SELECT id FROM sys_department WHERE code='PROD' AND deleted=0), 1),
    ('ESC-004', 'escort04', '$2y$10$abQC8jsR29shMtgIJjIUO.jMrKEClc.U1z/VoaKiB/PuDBPa0/d4K', '周强',   1, '13900000008', '跟车员',
        (SELECT id FROM sys_department WHERE code='PROD' AND deleted=0), 1)
ON CONFLICT DO NOTHING;

-- 2) 4 辆车（车牌 9924/6115/7744/7601），各配常态司机 + 常态跟车员
INSERT INTO vehicle (plate_no, default_driver_user_id, default_escort_user_id, status, remark) VALUES
    ('9924', (SELECT id FROM sys_user WHERE username='driver01' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort01' AND deleted=0), 1, '常态班组：张建国 / 陈伟'),
    ('6115', (SELECT id FROM sys_user WHERE username='driver02' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort02' AND deleted=0), 1, '常态班组：李志强 / 刘洋'),
    ('7744', (SELECT id FROM sys_user WHERE username='driver03' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort03' AND deleted=0), 1, '常态班组：王海涛 / 杨光'),
    ('7601', (SELECT id FROM sys_user WHERE username='driver04' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort04' AND deleted=0), 1, '常态班组：赵明 / 周强')
ON CONFLICT (plate_no, deleted) DO NOTHING;

-- 3) 最近 7 天（含今天）每名司机一条打卡；掺入迟到/缺勤/请假与一条跟车员替补，演示数据多样性
WITH crew AS (
    SELECT 1 AS idx,
           (SELECT id FROM sys_user WHERE username='driver01' AND deleted=0) AS driver_id,
           (SELECT id FROM vehicle  WHERE plate_no='9924'     AND deleted=0) AS vehicle_id,
           (SELECT id FROM sys_user WHERE username='escort01' AND deleted=0) AS escort_id
    UNION ALL SELECT 2, (SELECT id FROM sys_user WHERE username='driver02' AND deleted=0), (SELECT id FROM vehicle WHERE plate_no='6115' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort02' AND deleted=0)
    UNION ALL SELECT 3, (SELECT id FROM sys_user WHERE username='driver03' AND deleted=0), (SELECT id FROM vehicle WHERE plate_no='7744' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort03' AND deleted=0)
    UNION ALL SELECT 4, (SELECT id FROM sys_user WHERE username='driver04' AND deleted=0), (SELECT id FROM vehicle WHERE plate_no='7601' AND deleted=0), (SELECT id FROM sys_user WHERE username='escort04' AND deleted=0)
)
INSERT INTO driver_checkin (driver_user_id, vehicle_id, escort_user_id, escort_other, checkin_date, clock_in_at, clock_out_at, checkin_status, remark)
SELECT
    d.driver_id,
    d.vehicle_id,
    CASE WHEN g.day_offset = 2 AND d.idx = 1 THEN NULL ELSE d.escort_id END,
    CASE WHEN g.day_offset = 2 AND d.idx = 1 THEN '临时工-小马' ELSE NULL END,
    (CURRENT_DATE - g.day_offset),
    CASE
        WHEN g.day_offset = 3 AND d.idx = 3 THEN NULL
        WHEN g.day_offset = 4 AND d.idx = 4 THEN NULL
        WHEN g.day_offset = 1 AND d.idx = 2 THEN (CURRENT_DATE - g.day_offset) + TIME '09:15:00'
        ELSE (CURRENT_DATE - g.day_offset) + TIME '08:00:00'
    END,
    CASE
        WHEN g.day_offset = 3 AND d.idx = 3 THEN NULL
        WHEN g.day_offset = 4 AND d.idx = 4 THEN NULL
        ELSE (CURRENT_DATE - g.day_offset) + TIME '17:30:00'
    END,
    CASE
        WHEN g.day_offset = 1 AND d.idx = 2 THEN 'LATE'
        WHEN g.day_offset = 3 AND d.idx = 3 THEN 'ABSENT'
        WHEN g.day_offset = 4 AND d.idx = 4 THEN 'LEAVE'
        ELSE 'NORMAL'
    END,
    CASE
        WHEN g.day_offset = 2 AND d.idx = 1 THEN '陈伟请假，临时找小马跟车'
        WHEN g.day_offset = 3 AND d.idx = 3 THEN '王海涛缺勤'
        WHEN g.day_offset = 4 AND d.idx = 4 THEN '赵明请假'
        ELSE NULL
    END
FROM generate_series(0, 6) AS g(day_offset)
CROSS JOIN crew d
ON CONFLICT (driver_user_id, checkin_date, deleted) DO NOTHING;

-- 4) 少量打油示例（images 留空数组）
INSERT INTO fuel_record (vehicle_id, driver_user_id, fuel_date, liters, amount, unit_price, odometer, images, remark) VALUES
    ((SELECT id FROM vehicle WHERE plate_no='9924' AND deleted=0), (SELECT id FROM sys_user WHERE username='driver01' AND deleted=0), CURRENT_DATE - 1, 60.50, 480.00, 7.93, 125300.0, '[]'::jsonb, '加满'),
    ((SELECT id FROM vehicle WHERE plate_no='6115' AND deleted=0), (SELECT id FROM sys_user WHERE username='driver02' AND deleted=0), CURRENT_DATE - 3, 55.00, 436.70, 7.94, 98800.0,  '[]'::jsonb, NULL),
    ((SELECT id FROM vehicle WHERE plate_no='7744' AND deleted=0), (SELECT id FROM sys_user WHERE username='driver03' AND deleted=0), CURRENT_DATE - 5, 48.20, 382.50, 7.94, 76300.0,  '[]'::jsonb, NULL);
