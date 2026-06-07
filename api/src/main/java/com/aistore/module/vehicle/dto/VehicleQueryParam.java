package com.aistore.module.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 车辆列表查询参数 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleQueryParam {

    /** 关键词（模糊匹配车牌号） */
    private String keyword;

    /** 状态筛选（0=禁用，1=启用） */
    private Integer status;

    @Builder.Default
    private Integer page = 1;

    @Builder.Default
    private Integer pageSize = 20;
}
