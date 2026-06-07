package com.aistore.module.category.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 物料品类列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialCategoryQueryParam {
    private String keyword;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
