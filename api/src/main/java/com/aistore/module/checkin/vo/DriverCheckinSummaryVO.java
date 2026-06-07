package com.aistore.module.checkin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 打卡记录列表项 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverCheckinSummaryVO {
    private Long id;
    private Long driverUserId;
    private String driverName;
    private Long vehicleId;
    private String vehiclePlateNo;
    private String escortName;
    private LocalDate checkinDate;
    private LocalDateTime clockInAt;
    private LocalDateTime clockOutAt;
    private String checkinStatus;
}
