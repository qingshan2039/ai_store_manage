package com.aistore.module.inventory.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** 库存实体（表 inventory，基本单位记账） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("inventory")
public class Inventory {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private Long lpnId;
    private Long locationId;
    private String lotNo;
    private LocalDate mfgDate;
    private LocalDate expDate;
    private BigDecimal qtyOnHand;
    private BigDecimal qtyReserved;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
