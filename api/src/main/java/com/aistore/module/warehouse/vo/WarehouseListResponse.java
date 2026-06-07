package com.aistore.module.warehouse.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 仓库列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseListResponse {
    private List<WarehouseSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
