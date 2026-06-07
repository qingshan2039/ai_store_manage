package com.aistore.module.packaginglevel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 创建包装层级请求 DTO（对齐 CreatePackagingLevelRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePackagingLevelRequest {

    @NotNull(message = "所属 SKU 不能为空")
    private Long skuId;

    @NotBlank(message = "层级名称不能为空")
    @Size(min = 1, max = 32, message = "层级名称长度必须在1-32个字符之间")
    private String levelName;

    @NotNull(message = "层级序号不能为空")
    private Integer levelSeq;

    @NotBlank(message = "单位不能为空")
    @Size(min = 1, max = 16, message = "单位长度必须在1-16个字符之间")
    private String unitCode;

    @PositiveOrZero(message = "长度不能为负")
    private BigDecimal length;
    @PositiveOrZero(message = "宽度不能为负")
    private BigDecimal width;
    @PositiveOrZero(message = "高度不能为负")
    private BigDecimal height;
    @PositiveOrZero(message = "净重不能为负")
    private BigDecimal netWeight;
    @PositiveOrZero(message = "毛重不能为负")
    private BigDecimal grossWeight;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isBaseUnit;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isSellable;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
