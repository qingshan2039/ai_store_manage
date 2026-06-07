package com.aistore.module.packagingrelation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 创建包装关系请求 DTO（对齐 CreatePackagingRelationRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePackagingRelationRequest {

    @NotNull(message = "父层不能为空")
    private Long parentLevelId;

    @NotNull(message = "子层不能为空")
    private Long childLevelId;

    @NotNull(message = "含子层数量不能为空")
    @Positive(message = "含子层数量必须大于0")
    private BigDecimal childQty;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isFixedQty;

    @PositiveOrZero(message = "皮重不能为负")
    private BigDecimal tareWeight;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
