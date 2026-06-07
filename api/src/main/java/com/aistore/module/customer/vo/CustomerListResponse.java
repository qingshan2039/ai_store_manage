package com.aistore.module.customer.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 顾客列表分页响应体
 * 严格对齐 OpenAPI 契约 CustomerListResponse Schema
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerListResponse {

    private List<CustomerSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
