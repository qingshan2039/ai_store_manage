package com.aistore.module.customer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 顾客摘要响应 VO，用于列表展示
 * 严格对齐 OpenAPI 契约 CustomerSummary Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSummaryVO {

    private Long id;
    private String code;
    private String name;
    private String address;
    /** 收/发货地址（ship-to） */
    private String shipAddress;
    private String contact;
    private String phone;
    /** 状态：0=禁用，1=启用 */
    private Integer status;
    private LocalDateTime createdAt;
}
