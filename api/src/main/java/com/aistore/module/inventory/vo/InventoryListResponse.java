package com.aistore.module.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 库存列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryListResponse {
    private List<InventorySummaryItemVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
