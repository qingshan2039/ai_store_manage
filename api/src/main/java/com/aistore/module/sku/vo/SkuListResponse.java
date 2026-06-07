package com.aistore.module.sku.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** SKU 列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuListResponse {
    private List<SkuSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
