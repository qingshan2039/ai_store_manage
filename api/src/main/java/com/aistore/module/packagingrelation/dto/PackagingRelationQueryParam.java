package com.aistore.module.packagingrelation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 包装关系列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingRelationQueryParam {
    private Long parentLevelId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
