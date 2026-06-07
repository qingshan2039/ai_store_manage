package com.aistore.module.packaginglevel.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 更新包装层级请求 DTO（sku_id/level_seq 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePackagingLevelRequest {

    @Size(min = 1, max = 32, message = "层级名称长度必须在1-32个字符之间")
    private String levelName;

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
}
