package com.aistore.module.fuel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** 创建打油记录请求 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFuelRecordRequest {

    @NotNull(message = "车辆不能为空")
    private Long vehicleId;

    /** 打油司机用户ID（可空） */
    private Long driverUserId;

    @NotNull(message = "打油日期不能为空")
    private LocalDate fuelDate;

    @PositiveOrZero(message = "升数不能为负")
    private BigDecimal liters;

    @PositiveOrZero(message = "金额不能为负")
    private BigDecimal amount;

    @PositiveOrZero(message = "单价不能为负")
    private BigDecimal unitPrice;

    @PositiveOrZero(message = "里程不能为负")
    private BigDecimal odometer;

    /** 小票/凭证图片 URL 列表 */
    private List<String> images;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
