package com.aistore.module.sku.vo;

import com.aistore.module.sku.enums.ItemType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** SKU 列表项 VO（对齐 SkuSummary Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuSummaryVO {
    private Long id;
    private Long spuId;
    private String spuName;
    private String skuCode;
    private String skuName;
    private ItemType itemType;
    private BigDecimal lengthMm;
    private BigDecimal widthMm;
    private Integer status;
    private LocalDateTime createdAt;
}
