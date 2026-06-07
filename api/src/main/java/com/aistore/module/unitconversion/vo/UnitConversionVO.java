package com.aistore.module.unitconversion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 计量换算详情响应 VO（对齐 UnitConversion Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitConversionVO {
    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private String fromUnit;
    private String toUnit;
    private BigDecimal factor;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
