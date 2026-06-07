package com.aistore.module.pallet.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 更新托盘类型请求 DTO（所有字段可选；code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePalletTypeRequest {

    @Size(min = 1, max = 64, message = "托盘名称长度必须在1-64个字符之间")
    private String name;

    @Positive(message = "长度必须大于0")
    private BigDecimal length;

    @Positive(message = "宽度必须大于0")
    private BigDecimal width;

    @PositiveOrZero(message = "皮重不能为负")
    private BigDecimal tareWeight;

    @PositiveOrZero(message = "最大载重不能为负")
    private BigDecimal maxLoad;

    @Min(value = 1, message = "最大堆叠层至少为1")
    private Integer maxStack;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
