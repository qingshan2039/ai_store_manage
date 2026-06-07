package com.aistore.module.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/** 库存统计 VO（对齐 InventorySummary Schema）：库存数量 + 托盘数量 + 整托/尾托 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventorySummaryVO {
    private Long skuId;
    private String skuName;
    private BigDecimal totalQty;
    private BigDecimal totalReserved;
    private BigDecimal totalAvailable;
    private Integer palletCount;
    private Integer recordCount;
    private BigDecimal standardPalletQty;
    private List<InventoryPalletVO> pallets;
}
