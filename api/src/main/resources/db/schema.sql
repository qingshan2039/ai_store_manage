-- ============================================================
-- AI Store Manage - 用户管理模块建表 SQL
-- 数据库: MySQL 8.x
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_general_ci
-- ============================================================

-- 创建数据库（如不存在）
CREATE DATABASE IF NOT EXISTS ai_store_manage
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE ai_store_manage;

-- ============================================================
-- 表: sys_user
-- 说明: 系统用户表，承载登录、身份认证、权限关联的基础实体
-- ============================================================
DROP TABLE IF EXISTS sys_user;

CREATE TABLE sys_user (
    -- 基础字段
    id              BIGINT          NOT NULL AUTO_INCREMENT  COMMENT '主键 ID',
    employee_no     VARCHAR(32)     NOT NULL                 COMMENT 'HR 分配的工号，如 WH-20260001',
    username        VARCHAR(64)     NOT NULL                 COMMENT '登录账号',
    password        VARCHAR(255)    NOT NULL                 COMMENT '登录密码（BCrypt 加密存储）',

    -- 业务字段
    name            VARCHAR(64)     NOT NULL                 COMMENT '员工真实姓名',
    nickname        VARCHAR(64)     DEFAULT NULL             COMMENT '系统内显示名称',
    avatar          VARCHAR(512)    DEFAULT NULL             COMMENT '头像图片 URL',
    gender          TINYINT         NOT NULL DEFAULT 0       COMMENT '性别：0=未知，1=男，2=女',
    phone_number    VARCHAR(20)     NOT NULL                 COMMENT '手机号码',
    email           VARCHAR(128)    DEFAULT NULL             COMMENT '企业邮箱',
    job_title       VARCHAR(64)     DEFAULT NULL             COMMENT '职位名称',
    department_id   BIGINT          DEFAULT NULL             COMMENT '所属部门 ID',
    hide_phone_number TINYINT(1)    NOT NULL DEFAULT 0       COMMENT '是否隐藏手机号：0=否，1=是',
    hide_name       TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '是否隐藏姓名：0=否，1=是',
    remark          VARCHAR(500)    DEFAULT NULL             COMMENT '管理员备注',

    -- 状态字段
    status          TINYINT         NOT NULL DEFAULT 1       COMMENT '账号状态：0=禁用，1=启用',

    -- 审计字段
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP          COMMENT '创建时间',
    updated_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    created_by      BIGINT          DEFAULT NULL             COMMENT '创建人 ID',
    updated_by      BIGINT          DEFAULT NULL             COMMENT '更新人 ID',
    deleted         TINYINT(1)      NOT NULL DEFAULT 0       COMMENT '逻辑删除：0=未删除，1=已删除',

    -- 主键
    PRIMARY KEY (id),

    -- 唯一索引（含 deleted，逻辑删除后允许复用 username / employee_no）
    UNIQUE INDEX uk_username_deleted (username, deleted),
    UNIQUE INDEX uk_employee_no_deleted (employee_no, deleted),

    -- 普通索引
    INDEX idx_phone_number (phone_number),
    INDEX idx_department_id (department_id),
    INDEX idx_status (status),
    INDEX idx_name (name)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci
  COMMENT='系统用户表';
