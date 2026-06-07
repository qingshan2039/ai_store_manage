package com.aistore.module.fuel.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** 更新打油记录请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateFuelRecordRequest {

    private Long vehicleId;

    private Long driverUserId;

    private LocalDate fuelDate;

    @PositiveOrZero(message = "升数不能为负")
    private BigDecimal liters;

    @PositiveOrZero(message = "金额不能为负")
    private BigDecimal amount;

    @PositiveOrZero(message = "单价不能为负")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "里程不能为负")
    private BigDecimal odometer;

    private List<String> images;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
