package com.aistore.module.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 库存详情响应 VO（对齐 Inventory Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryVO {
    private Long id;
    private Long skuId;
    private String skuCode;
    private String skuName;
    private Long lpnId;
    private String lpnCode;
    private Long locationId;
    private String locationCode;
    private String lotNo;
    private LocalDate mfgDate;
    private LocalDate expDate;
    private BigDecimal qtyOnHand;
    private BigDecimal qtyReserved;
    private BigDecimal qtyAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;
}
