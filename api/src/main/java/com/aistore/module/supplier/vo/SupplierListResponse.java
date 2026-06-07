package com.aistore.module.supplier.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 供应商列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierListResponse {
    private List<SupplierSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
