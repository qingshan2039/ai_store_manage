package com.aistore.module.fuel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** 打油记录详情响应 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelRecordVO {
    private Long id;
    private Long vehicleId;
    private String vehiclePlateNo;
    private Long driverUserId;
    private String driverName;
    private LocalDate fuelDate;
    private BigDecimal liters;
    private BigDecimal amount;
    private BigDecimal unitPrice;
    private BigDecimal odometer;
    private List<String> images;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
