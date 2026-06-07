package com.aistore.module.packagingrelation.entity;

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

/** 包装关系实体（表 packaging_relation） */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("packaging_relation")
public class PackagingRelation {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long parentLevelId;
    private Long childLevelId;
    private BigDecimal childQty;
    private Integer isFixedQty;
    private BigDecimal tareWeight;
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
