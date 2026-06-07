package com.aistore.module.unitconversion.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 计量换算列表项 VO（对齐 UnitConversionSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnitConversionSummaryVO {
    private Long id;
    private Long skuId;
    private String skuName;
    private String fromUnit;
    private String toUnit;
    private BigDecimal factor;
    private Integer status;
    private LocalDateTime createdAt;
}
