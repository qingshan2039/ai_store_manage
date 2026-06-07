package com.aistore.module.pallet.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 创建托盘类型请求 DTO（对齐 CreatePalletTypeRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePalletTypeRequest {

    @NotBlank(message = "托盘编码不能为空")
    @Size(min = 2, max = 32, message = "托盘编码长度必须在2-32个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "托盘编码只能包含字母、数字和连字符")
    private String code;

    @NotBlank(message = "托盘名称不能为空")
    @Size(min = 1, max = 64, message = "托盘名称长度必须在1-64个字符之间")
    private String name;

    @NotNull(message = "长度不能为空")
    @Positive(message = "长度必须大于0")
    private BigDecimal length;

    @NotNull(message = "宽度不能为空")
    @Positive(message = "宽度必须大于0")
    private BigDecimal width;

    @PositiveOrZero(message = "皮重不能为负")
    private BigDecimal tareWeight;

    @PositiveOrZero(message = "最大载重不能为负")
    private BigDecimal maxLoad;

    @Min(value = 1, message = "最大堆叠层至少为1")
    private Integer maxStack;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remark;
}
