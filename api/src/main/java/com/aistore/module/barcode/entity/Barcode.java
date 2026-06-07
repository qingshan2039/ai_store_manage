package com.aistore.module.barcode.entity;

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

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 条码实体（表 barcode） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("barcode")
public class Barcode {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long levelId;
    private String barcode;
    /** 条码类型，存枚举名 */
    private String barcodeType;
    private Integer isPrimary;
    private LocalDate validFrom;
    private LocalDate validTo;
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
