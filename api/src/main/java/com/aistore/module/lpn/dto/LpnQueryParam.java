package com.aistore.module.lpn.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 托盘实例列表查询参数（status 以枚举名字符串传入 Mapper） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LpnQueryParam {
    private String keyword;
    private Long warehouseId;
    private String status;
    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer pageSize = 20;
}
