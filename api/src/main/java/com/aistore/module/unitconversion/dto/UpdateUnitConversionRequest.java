package com.aistore.module.unitconversion.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 更新计量换算请求 DTO（sku/单位不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUnitConversionRequest {

    @Positive(message = "换算系数必须大于0")
    private BigDecimal factor;
}
