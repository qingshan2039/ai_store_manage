package com.aistore.module.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** 库存列表项 VO（对齐 InventorySummaryItem Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryItemVO {
    private Long id;
    private Long skuId;
    private String skuName;
    private String lpnCode;
    private String palletTypeName;
    private String locationCode;
    private String lotNo;
    private BigDecimal qtyOnHand;
    private BigDecimal qtyReserved;
    private BigDecimal qtyAvailable;
    private LocalDateTime createdAt;
}
