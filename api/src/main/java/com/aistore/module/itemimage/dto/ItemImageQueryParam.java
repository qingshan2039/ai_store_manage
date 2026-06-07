package com.aistore.module.itemimage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 物料图片列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemImageQueryParam {
    private Long spuId;
    private Long skuId;
    private Long levelId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
