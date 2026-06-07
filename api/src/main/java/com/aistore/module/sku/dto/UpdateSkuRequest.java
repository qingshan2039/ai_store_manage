package com.aistore.module.sku.dto;

import com.aistore.module.sku.enums.ItemType;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/** 更新 SKU 请求 DTO（spu_id/sku_code 不可改，状态走独立接口） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSkuRequest {

    @Size(min = 1, max = 128, message = "SKU 名称长度必须在1-128个字符之间")
    private String skuName;

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

    private Map<String, Object> spec;
}
