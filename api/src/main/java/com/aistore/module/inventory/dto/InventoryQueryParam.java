package com.aistore.module.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 库存列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryQueryParam {
    private Long skuId;
    private Long lpnId;
    private Long locationId;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
