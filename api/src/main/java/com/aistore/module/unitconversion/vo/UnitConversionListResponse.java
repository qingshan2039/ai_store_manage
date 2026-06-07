package com.aistore.module.unitconversion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 计量换算列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitConversionListResponse {
    private List<UnitConversionSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
