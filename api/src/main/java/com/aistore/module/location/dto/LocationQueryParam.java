package com.aistore.module.location.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 库位列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationQueryParam {
    private String keyword;
    private Long warehouseId;
    private Long zoneId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
