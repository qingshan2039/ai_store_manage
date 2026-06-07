package com.aistore.module.unitconversion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 创建计量换算请求 DTO（对齐 CreateUnitConversionRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUnitConversionRequest {

    @NotNull(message = "所属 SKU 不能为空")
    private Long skuId;

    @NotBlank(message = "源单位不能为空")
    @Size(min = 1, max = 16, message = "源单位长度必须在1-16个字符之间")
    private String fromUnit;

    @NotBlank(message = "目标单位不能为空")
    @Size(min = 1, max = 16, message = "目标单位长度必须在1-16个字符之间")
    private String toUnit;

    @NotNull(message = "换算系数不能为空")
    @Positive(message = "换算系数必须大于0")
    private BigDecimal factor;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
