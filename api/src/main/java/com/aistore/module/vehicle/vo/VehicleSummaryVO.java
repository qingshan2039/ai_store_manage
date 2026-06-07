package com.aistore.module.vehicle.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 车辆列表项 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleSummaryVO {
    private Long id;
    private String plateNo;
    private String defaultDriverName;
    private String defaultEscortName;
    private Integer status;
    private LocalDateTime createdAt;
}
