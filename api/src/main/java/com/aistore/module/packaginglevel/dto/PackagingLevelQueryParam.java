package com.aistore.module.packaginglevel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 包装层级列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackagingLevelQueryParam {
    private Long skuId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
