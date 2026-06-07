package com.aistore.module.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 顾客列表查询参数
 * 严格对齐 OpenAPI 契约 listCustomers 接口的 parameters 定义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerQueryParam {

    /**
     * 关键词搜索（模糊匹配名称、编码、联系人）
     */
    private String keyword;

    /**
     * 状态筛选（0=禁用，1=启用）
     */
    private Integer status;

    /**
     * 页码，从 1 开始，默认 1
     */
    @Builder.Default
    private Integer page = 1;

    /**
     * 每页条数，默认 20
     */
    @Builder.Default
    private Integer pageSize = 20;
}
