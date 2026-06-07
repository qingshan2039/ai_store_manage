-- ============================================================
-- Flyway 基线迁移 V1：初始 schema（PostgreSQL）
-- AI Store Manage - 用户表 + 部门表 + 部门种子数据
--
-- 说明：
--   本迁移代表项目当前完整 schema 的基线。
--   对已存在这些表但无 Flyway 历史的库，借助 baseline-on-migrate 建立基线（本脚本不重复执行）；
--   对全新空库，本脚本负责建表与灌入部门种子。
--   今后的 schema 变更请新增 V2__xxx.sql、V3__xxx.sql 等递增迁移，禁止修改已发布的迁移文件。
-- ============================================================

-- ------------------------------------------------------------
-- 表: sys_user —— 系统用户表
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_user (
    id                BIGSERIAL    PRIMARY KEY,
    employee_no       VARCHAR(32)  NOT NULL,
    username          VARCHAR(64)  NOT NULL,
    password          VARCHAR(255) NOT NULL,
    name              VARCHAR(64)  NOT NULL,
    nickname          VARCHAR(64),
    avatar            VARCHAR(512),
    gender            SMALLINT     NOT NULL DEFAULT 0,
    phone_number      VARCHAR(20)  NOT NULL,
    email             VARCHAR(128),
    job_title         VARCHAR(64),
    department_id     BIGINT,
    hide_phone_number BOOLEAN      NOT NULL DEFAULT FALSE,
    hide_name         BOOLEAN      NOT NULL DEFAULT FALSE,
    remark            VARCHAR(500),
    status            SMALLINT     NOT NULL DEFAULT 1,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    deleted           SMALLINT     NOT NULL DEFAULT 0,
    -- 唯一约束（含 deleted，逻辑删除后允许复用 username / employee_no）
    CONSTRAINT uk_user_username_deleted    UNIQUE (username, deleted),
    CONSTRAINT uk_user_employee_no_deleted UNIQUE (employee_no, deleted)
);

CREATE INDEX IF NOT EXISTS idx_user_phone_number  ON sys_user (phone_number);
CREATE INDEX IF NOT EXISTS idx_user_department_id ON sys_user (department_id);
CREATE INDEX IF NOT EXISTS idx_user_status        ON sys_user (status);
CREATE INDEX IF NOT EXISTS idx_user_name          ON sys_user (name);

COMMENT ON TABLE  sys_user            IS '系统用户表';
COMMENT ON COLUMN sys_user.gender     IS '性别：0=未知，1=男，2=女';
COMMENT ON COLUMN sys_user.status     IS '账号状态：0=禁用，1=启用';
COMMENT ON COLUMN sys_user.deleted    IS '逻辑删除：0=未删除，1=已删除';

-- ------------------------------------------------------------
-- 表: sys_department —— 部门表（扁平结构，按 type 分类）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS sys_department (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(64)  NOT NULL,
    code        VARCHAR(32)  NOT NULL,
    type        VARCHAR(32)  NOT NULL,
    status      SMALLINT     NOT NULL DEFAULT 1,
    sort        INTEGER      NOT NULL DEFAULT 0,
    remark      VARCHAR(500),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by  BIGINT,
    updated_by  BIGINT,
    deleted     SMALLINT     NOT NULL DEFAULT 0,
    CONSTRAINT uk_dept_name_deleted UNIQUE (name, deleted),
    CONSTRAINT uk_dept_code_deleted UNIQUE (code, deleted)
);

CREATE INDEX IF NOT EXISTS idx_dept_type   ON sys_department (type);
CREATE INDEX IF NOT EXISTS idx_dept_status ON sys_department (status);

COMMENT ON TABLE  sys_department         IS '部门表';
COMMENT ON COLUMN sys_department.type    IS '部门类型：WAREHOUSE/TRANSPORT/SALES/PRODUCTION/OFFICE/HR/FINANCE/MANAGEMENT';
COMMENT ON COLUMN sys_department.status  IS '状态：0=禁用，1=启用';
COMMENT ON COLUMN sys_department.deleted IS '逻辑删除：0=未删除，1=已删除';

-- 初始化部门数据（8 类：7 个业务部门 + 管理层）；ON CONFLICT DO NOTHING 保证可重复执行
INSERT INTO sys_department (name, code, type, status, sort, remark) VALUES
    ('仓储管理部', 'WH',     'WAREHOUSE',  1, 1, '负责仓库收发存管理'),
    ('运输部',     'TRANS',  'TRANSPORT',  1, 2, '负责物流配送与运输'),
    ('销售部',     'SALES',  'SALES',      1, 3, '负责销售与客户管理'),
    ('生产部',     'PROD',   'PRODUCTION', 1, 4, '负责生产制造'),
    ('行政办公室', 'OFFICE', 'OFFICE',     1, 5, '负责行政与综合办公'),
    ('人事部',     'HR',     'HR',         1, 6, '负责人力资源管理'),
    ('财务部',     'FIN',    'FINANCE',    1, 7, '负责财务与会计'),
    ('管理层',     'MGMT',   'MANAGEMENT', 1, 8, '老板 / 高层管理')
ON CONFLICT DO NOTHING;
