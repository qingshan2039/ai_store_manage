package com.aistore.module.fuel.entity;

import com.aistore.common.handler.JsonbStringListTypeHandler;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 打油（加油）记录实体（表 fuel_record）。
 * images 为 PostgreSQL jsonb（图片 URL 数组），autoResultMap 让 BaseMapper 套用类型处理器；
 * 可空字段标注 updateStrategy=IGNORED，使更新时可写 null（如清空图片/司机）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "fuel_record", autoResultMap = true)
public class FuelRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long vehicleId;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private Long driverUserId;

    private LocalDate fuelDate;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal liters;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal amount;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal unitPrice;
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private BigDecimal odometer;

    @TableField(typeHandler = JsonbStringListTypeHandler.class, jdbcType = JdbcType.OTHER,
            updateStrategy = FieldStrategy.IGNORED)
    private List<String> images;

    private String remark;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
