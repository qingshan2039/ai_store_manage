package com.aistore.module.packagingrelation.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 更新包装关系请求 DTO（父子层不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePackagingRelationRequest {

    @Positive(message = "含子层数量必须大于0")
    private BigDecimal childQty;

    @Min(value = 0, message = "取值无效")
    @Max(value = 1, message = "取值无效")
    private Integer isFixedQty;

    @PositiveOrZero(message = "皮重不能为负")
    private BigDecimal tareWeight;
}
