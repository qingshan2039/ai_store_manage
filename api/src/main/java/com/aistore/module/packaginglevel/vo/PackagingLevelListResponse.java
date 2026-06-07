package com.aistore.module.packaginglevel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 包装层级列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingLevelListResponse {
    private List<PackagingLevelSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
