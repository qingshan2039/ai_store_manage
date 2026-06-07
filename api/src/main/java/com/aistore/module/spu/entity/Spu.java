package com.aistore.module.spu.entity;

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

import java.time.LocalDateTime;

/** 标准产品单元实体（表 spu，品类层） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("spu")
public class Spu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String spuCode;
    private String spuName;
    private String categoryCode;
    private String brand;
    private String baseUnit;
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
