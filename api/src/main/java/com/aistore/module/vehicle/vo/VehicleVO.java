package com.aistore.module.vehicle.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** 车辆详情响应 VO（含常态司机/跟车员显示名） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VehicleVO {
    private Long id;
    private String plateNo;
    private Long defaultDriverUserId;
    private String defaultDriverOther;
    /** 常态司机显示名（在册用户名或替补名） */
    private String defaultDriverName;
    private Long defaultEscortUserId;
    private String defaultEscortOther;
    /** 常态跟车员显示名（在册用户名或替补名） */
    private String defaultEscortName;
    private String remark;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
