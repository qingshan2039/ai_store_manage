package com.aistore.module.sku.entity;

import com.aistore.common.handler.JsonbTypeHandler;
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
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/** 最小库存单元实体（表 sku）。spec 为 PostgreSQL jsonb，autoResultMap 让 BaseMapper 套用类型处理器。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "sku", autoResultMap = true)
public class Sku {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long spuId;
    private String skuCode;
    private String skuName;
    /** 阶段类型，存枚举名 RAW/SEMI/FINISHED */
    private String itemType;
    private BigDecimal lengthMm;
    private BigDecimal widthMm;
    private BigDecimal thicknessMm;
    private BigDecimal rollLengthM;
    private String color;
    private BigDecimal gsm;

    @TableField(typeHandler = JsonbTypeHandler.class, jdbcType = JdbcType.OTHER)
    private Map<String, Object> spec;

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
