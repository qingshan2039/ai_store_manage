package com.aistore.module.sku.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** SKU 列表查询参数（itemType 以枚举名字符串传入 Mapper） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuQueryParam {
    private String keyword;
    private Long spuId;
    private String itemType;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
