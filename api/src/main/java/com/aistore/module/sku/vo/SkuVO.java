package com.aistore.module.sku.vo;

import com.aistore.module.sku.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** SKU 详情响应 VO（对齐 Sku Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuVO {
    private Long id;
    private Long spuId;
    private String spuCode;
    private String spuName;
    private String skuCode;
    private String skuName;
    private ItemType itemType;
    private BigDecimal lengthMm;
    private BigDecimal widthMm;
    private BigDecimal thicknessMm;
    private BigDecimal rollLengthM;
    private String color;
    private BigDecimal gsm;
    private Map<String, Object> spec;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
