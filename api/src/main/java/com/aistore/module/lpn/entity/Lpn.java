package com.aistore.module.lpn.entity;

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

/** 托盘实例实体（表 lpn）。status 存 LpnStatus 枚举名。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("lpn")
public class Lpn {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String lpnCode;
    private Long palletTypeId;
    private Long warehouseId;
    private Long locationId;
    private String status;
    private BigDecimal grossWeight;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    private Long createdBy;
    private Long updatedBy;

    @TableLogic
    private Integer deleted;
}
