package com.aistore.module.warehouse.dto;

import com.aistore.module.warehouse.enums.WarehouseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 仓库列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseQueryParam {
    private String keyword;
    private WarehouseType type;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
