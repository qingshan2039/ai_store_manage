package com.aistore.module.sku.dto;

import com.aistore.module.sku.enums.ItemType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/** 创建 SKU 请求 DTO（对齐 CreateSkuRequest Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSkuRequest {

    @NotNull(message = "所属 SPU 不能为空")
    private Long spuId;

    @NotBlank(message = "SKU 编码不能为空")
    @Size(min = 2, max = 48, message = "SKU 编码长度必须在2-48个字符之间")
    @Pattern(regexp = "^[A-Za-z0-9\\-]+$", message = "SKU 编码只能包含字母、数字和连字符")
    private String skuCode;

    @NotBlank(message = "SKU 名称不能为空")
    @Size(min = 1, max = 128, message = "SKU 名称长度必须在1-128个字符之间")
    private String skuName;

    @NotNull(message = "阶段类型不能为空")
    private ItemType itemType;

    @PositiveOrZero(message = "长度不能为负")
    private BigDecimal lengthMm;

    @PositiveOrZero(message = "宽度不能为负")
    private BigDecimal widthMm;

    @PositiveOrZero(message = "厚度不能为负")
    private BigDecimal thicknessMm;

    @PositiveOrZero(message = "卷长不能为负")
    private BigDecimal rollLengthM;

    @Size(max = 32, message = "颜色长度不能超过32个字符")
    private String color;

    @PositiveOrZero(message = "克重不能为负")
    private BigDecimal gsm;

    /** 同尺寸下的细分规格（材质/牌号/工艺等），存 jsonb */
    private Map<String, Object> spec;

    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;
}
