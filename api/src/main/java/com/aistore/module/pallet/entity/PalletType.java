package com.aistore.module.pallet.entity;

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
import java.time.LocalDateTime;

/** 托盘类型实体（表 pallet_type，ISO 规格） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("pallet_type")
public class PalletType {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String code;
    private String name;
    /** 长(mm) */
    private BigDecimal length;
    /** 宽(mm) */
    private BigDecimal width;
    /** 皮重(kg) */
    private BigDecimal tareWeight;
    /** 最大载重(kg) */
    private BigDecimal maxLoad;
    /** 最大堆叠层 */
    private Integer maxStack;
    private String remark;
    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
