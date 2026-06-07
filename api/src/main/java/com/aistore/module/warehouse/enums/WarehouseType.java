package com.aistore.module.warehouse.enums;

/**
 * 仓库类型枚举
 * 严格对齐 OpenAPI 契约 WarehouseType Schema
 */
public enum WarehouseType {
    /** 原料库 */
    RAW,
    /** 半成品库 */
    SEMI,
    /** 成品库 */
    FINISHED
}
