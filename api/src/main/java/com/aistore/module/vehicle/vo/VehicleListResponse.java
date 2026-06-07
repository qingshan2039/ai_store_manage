package com.aistore.module.vehicle.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 车辆列表分页响应体 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleListResponse {
    private List<VehicleSummaryVO> items;
    private Long total;
    private Integer page;
    private Integer pageSize;
    private Integer totalPages;
}
