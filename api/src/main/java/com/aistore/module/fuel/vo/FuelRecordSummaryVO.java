package com.aistore.module.fuel.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 打油记录列表项 VO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FuelRecordSummaryVO {
    private Long id;
    private Long vehicleId;
    private String vehiclePlateNo;
    private String driverName;
    private LocalDate fuelDate;
    private BigDecimal liters;
    private BigDecimal amount;
    private Integer imageCount;
    private LocalDateTime createdAt;
}
