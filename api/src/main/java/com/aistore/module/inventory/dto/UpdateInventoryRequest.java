package com.aistore.module.inventory.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/** 更新库存记录请求 DTO（sku 不可改） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryRequest {

    private Long lpnId;
    private Long locationId;

    @Size(max = 64, message = "批次号长度不能超过64个字符")
    private String lotNo;

    private LocalDate mfgDate;
    private LocalDate expDate;

    @PositiveOrZero(message = "在库数量不能为负")
    private BigDecimal qtyOnHand;

    @PositiveOrZero(message = "锁定数量不能为负")
    private BigDecimal qtyReserved;
}
