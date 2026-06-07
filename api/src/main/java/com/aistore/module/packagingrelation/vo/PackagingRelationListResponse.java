package com.aistore.module.packagingrelation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 包装关系列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingRelationListResponse {
    private List<PackagingRelationSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
