package com.aistore.module.zone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 库区列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZoneQueryParam {
    private String keyword;
    private Long warehouseId;
    private Integer status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
