package com.aistore.module.fuel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 打油记录列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelRecordListResponse {
    private List<FuelRecordSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
