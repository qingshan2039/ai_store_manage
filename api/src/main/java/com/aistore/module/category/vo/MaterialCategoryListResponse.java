package com.aistore.module.category.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 物料品类列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryListResponse {
    private List<MaterialCategorySummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
