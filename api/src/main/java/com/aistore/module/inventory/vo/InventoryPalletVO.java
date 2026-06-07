package com.aistore.module.inventory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/** 库存统计-每托明细 VO（对齐 InventoryPallet Schema） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryPalletVO {
    private Long lpnId;
    private String lpnCode;
    private BigDecimal qty;
    /** 是否整托（对比标准每托数；无标准则为 null） */
    private Boolean fullPallet;
}
