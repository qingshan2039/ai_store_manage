package com.aistore.module.checkin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 打卡记录详情响应 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCheckinVO {
    private Long id;
    private Long driverUserId;
    private String driverOther;
    /** 司机显示名（在册用户名或替补名） */
    private String driverName;
    private Long vehicleId;
    private String vehiclePlateNo;
    private Long escortUserId;
    private String escortOther;
    /** 跟车员显示名（在册用户名或替补名） */
    private String escortName;
    private LocalDate checkinDate;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;
    private String checkinStatus;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
