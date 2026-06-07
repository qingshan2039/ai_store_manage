package com.aistore.module.packaginglevel.entity;

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

/** 包装层级实体（表 packaging_level） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("packaging_level")
public class PackagingLevel {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long skuId;
    private String levelName;
    private Integer levelSeq;
    private String unitCode;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private BigDecimal netWeight;
    private BigDecimal grossWeight;
    private Integer isBaseUnit;
    private Integer isSellable;
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
